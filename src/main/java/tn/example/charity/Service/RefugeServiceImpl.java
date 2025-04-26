package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Refuge;
import tn.example.charity.Repository.RefugeRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RefugeServiceImpl implements IRefugeService {

    RefugeRepository refugeRepository;

    public Refuge addRefuge(Refuge refuge) {
        return refugeRepository.save(refuge);
    }

    public List<Refuge> getAllRefuge() {
        List<Refuge> refugeList = refugeRepository.findAll();
        return refugeList;
    }

    public Refuge getRefugeById(Long idRfg) {
        Refuge refuge = refugeRepository.findById(idRfg).get();
        return refuge;
    }

    public void deleteRefugeById(Long idRfg) {
        refugeRepository.deleteById(idRfg);

    }

    public Refuge updateRefugeById(Refuge refuge) {
        return refugeRepository.save(refuge);
    }

    public Refuge getLastRefuge() {
        return refugeRepository.findTopByOrderByIdRfgDesc();

    }
}
