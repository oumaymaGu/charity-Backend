package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.SuiviDon;
import tn.example.charity.Repository.SuiviDonRepository;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@AllArgsConstructor
@Slf4j

public class SuiviDonServiceImpl implements ISuiviDonService{

    SuiviDonRepository suiviDonRepository;



    @Override
    public SuiviDon addSuiviDon(SuiviDon suiviDon) {
        return suiviDonRepository.save(suiviDon);
    }

    @Override

    public void deleteSuiviDon(Long idSuivi) {
        suiviDonRepository.deleteById(idSuivi);

    }

    @Override
    public SuiviDon modifySuiviDon(SuiviDon suiviDon) {
        return suiviDonRepository.save( suiviDon);
    }

    @Override
    public List<SuiviDon> getAllSuiviDon() {
        return suiviDonRepository.findAll();
    }

    @Override
    public List<SuiviDon> retreiveallSuiviDon(SuiviDon suiviDon) {
        return null;
    }

    @Override
    public SuiviDon retrieveallSuiviDonbyid(Long idSuivi) {
        return suiviDonRepository.findById(idSuivi).get();
    }


}





