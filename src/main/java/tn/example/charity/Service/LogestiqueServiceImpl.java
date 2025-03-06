package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Logestique;
import tn.example.charity.Repository.LogestiqueRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LogestiqueServiceImpl implements  ILogestiqueService{
    @Autowired
    private LogestiqueRepository logestiqueRepository;

    @Override
    public Logestique addlogestique(Logestique logestique) {
        return logestiqueRepository.save(logestique);
    }

    @Override
    public void deleteLogestique(Long idlogestique) {
        logestiqueRepository.deleteById(idlogestique);

    }

    @Override
    public Logestique modifylog(Logestique logestique) {
        return logestiqueRepository.save(logestique);
    }

    @Override
    public List<Logestique> getAlllogs() {
        return logestiqueRepository.findAll();
    }

    @Override
    public Logestique retrievelogbyid(Long idlogestique) {
        return logestiqueRepository.findById(idlogestique).get();
    }
    public List<Logestique> retrievelogbyname(String ressourceName) {
        return logestiqueRepository.findByRessourceName(ressourceName);
    }
}
