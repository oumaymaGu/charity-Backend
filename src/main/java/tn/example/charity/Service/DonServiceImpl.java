package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.TypeDon;
import tn.example.charity.Repository.DonRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class DonServiceImpl implements IDonService {

    private final DonRepository donRepository;
    private final NotificationService notificationService;

    @Override
    public Don addDon(Don don) {
        int newQuantity = (don.getQuantity() == null || don.getQuantity() <= 0) ? 1 : don.getQuantity();
        don.setQuantity(newQuantity);

        if (don.getTypeDon() == TypeDon.MATERIEL && don.getPhotoUrl() != null && !don.getPhotoUrl().isEmpty()) {
            Optional<Don> existingDonOpt;
            if ("MEDICAMENT".equalsIgnoreCase(don.getCategory())) {
                existingDonOpt = donRepository.findFirstByPhotoUrlAndCategoryAndMedicationNameAndLotNumber(
                        don.getPhotoUrl(), don.getCategory(), don.getMedicationName(), don.getLotNumber());
            } else {
                existingDonOpt = donRepository.findFirstByPhotoUrlAndCategory(don.getPhotoUrl(), don.getCategory());
            }

            if (existingDonOpt.isPresent()) {
                Don existingDon = existingDonOpt.get();
                int existingQuantity = existingDon.getQuantity() != null ? existingDon.getQuantity() : 0;
                existingDon.setQuantity(existingQuantity + newQuantity);
                if (don.getDateDon() != null && (existingDon.getDateDon() == null || don.getDateDon().after(existingDon.getDateDon()))) {
                    existingDon.setDateDon(don.getDateDon());
                }
                log.info("Mise à jour du don existant. Nouvelle quantité : {}", existingDon.getQuantity());
                return donRepository.save(existingDon);
            }
        }

        Don savedDon = donRepository.save(don);
        notificationService.createAndSendDonNotification(savedDon);
        log.info("Nouveau don créé avec ID {}", savedDon.getIdDon());
        return savedDon;
    }

    @Override
    public Optional<Don> findByImageHash(String imageHash) {
        return donRepository.findFirstByImageHash(imageHash);
    }

    @Override
    public void deleteDon(Long idDon) {
        donRepository.deleteById(idDon);
        log.info("Don supprimé avec ID {}", idDon);
    }

    @Override
    public Don modifyDon(Don don) {
        return donRepository.save(don);
    }

    @Override
    public List<Don> getAllDon() {
        return donRepository.findAll();
    }

    @Override
    public List<Don> retreiveallDon(Don don) {
        return null;
    }

    @Override
    public Don retrieveallDonbyid(Long idDon) {
        return donRepository.findById(idDon).orElse(null);
    }
}