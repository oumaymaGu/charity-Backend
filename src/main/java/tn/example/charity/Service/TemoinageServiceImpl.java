package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Entity.TemoinageStatut;
import tn.example.charity.Repository.TemoinageRepository;

import java.util.List;
import java.util.Optional;



@Service
@AllArgsConstructor
@Slf4j
public class TemoinageServiceImpl implements ITemoinageService {
    private final TemoinageRepository temoinageRepository;

    @Override
    public Temoinage addTemoinage(Temoinage temoinage ){
        // Si la description en anglais n'est pas fournie, tu peux la remplir ici
        if (temoinage.getDescription_en() == null || temoinage.getDescription_en().isEmpty()) {
            temoinage.setDescription_en(translateToEnglish(temoinage.getDescription())); // Traduction manuelle ou API
        }

        return temoinageRepository.save(temoinage);
    }

    private String translateToEnglish(String description) {
        return description + " (translated to English)";
    }


    @Override
    public void deleteTemoinage(Long idTemoin) {
        temoinageRepository.deleteById(idTemoin);
    }

    @Override
    public Temoinage modifyTemoinage(Temoinage temoinage) {
        return temoinageRepository.save(temoinage);
    }

    @Override
    public List<Temoinage> getAllTemoinages() {
        return temoinageRepository.findAll();
    }

    @Override
    public Temoinage getTemoinageById(Long idTemoin) {
        return temoinageRepository.findById(idTemoin).orElse(null);
    }

    @Override
    public List<Temoinage> getTemoinagesByStatut(TemoinageStatut temoinageStatut) {
        return temoinageRepository.findByStatut(temoinageStatut);
    }


    public List<Temoinage> trouverParStatut(String statut) {
        // Convertir la chaîne en TemoinageStatut
        TemoinageStatut statutEnum = TemoinageStatut.valueOf(statut.toUpperCase());

        // Utiliser le repository pour trouver les témoignages par statut
        return temoinageRepository.findByStatut(statutEnum);
    }
}
