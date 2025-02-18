package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Logement;
import tn.example.charity.Repository.LogementRepository;

import java.util.List;
@Service
@AllArgsConstructor
public class LogementServiceImpl implements ILogementService {

    LogementRepository repositorylog;

    public Logement addLogement(Logement logement) {
        return repositorylog.save(logement);
    }

    public List<Logement> getAllLogement() {
        List<Logement> logements = repositorylog.findAll();
        return logements;
    }

    public Logement getLogementById(Long idLog) {
        Logement logement = repositorylog.findById(idLog).get();
        return logement;
    }

    public Logement updateLogement(Logement logement) {
        return repositorylog.save(logement);
    }

    public void deleteLogement(Long idLog) {
        repositorylog.deleteById(idLog);

    }
}
