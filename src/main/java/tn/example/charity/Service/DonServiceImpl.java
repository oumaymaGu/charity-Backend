package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Don;
import tn.example.charity.Repository.DonRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j

public class DonServiceImpl implements IDonService {
    DonRepository donRepository;

    @Override
    public Don addDon(Don don) {
        return donRepository.save(don);
    }

    @Override
    public void deleteDon(Long idDon) {
        donRepository.deleteById(idDon);

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

        return donRepository.findById(idDon).get();
    }
}
