package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Repository.TemoinageRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TemoinageServiceImpl implements ITemoinageService {
    private final TemoinageRepository temoinageRepository;

    @Override
    public Temoinage addTemoinage(Temoinage temoinage) {
        return temoinageRepository.save(temoinage);
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
}
