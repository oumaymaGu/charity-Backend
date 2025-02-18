package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Repository.TemoinageRepository;

import java.util.List;
@Service
@AllArgsConstructor
@Slf4j
public class TemoinageServiceImpl implements ITemoinageService {
    TemoinageRepository temoinageRepository;
    public Temoinage addTemoinage(Temoinage temoinage) {
        return temoinageRepository.save(temoinage);
    }

    public void deleteTemoinage(Long idtemoinage) {
      temoinageRepository.deleteById(idtemoinage);
    }

    public Temoinage modifyTemoinage(Temoinage temoinage) {
        return temoinageRepository.save(temoinage);
    }

    public List<Temoinage> getAllTemoingage() {
        return temoinageRepository.findAll();
    }

    public List<Temoinage> retreiveallTemoignage(Temoinage temoignage) {
        return List.of();
    }

    public Temoinage retrieveallTemoignagebyid(Long idTemoinage) {
        return temoinageRepository.findById(idTemoinage).get();
    }
}
