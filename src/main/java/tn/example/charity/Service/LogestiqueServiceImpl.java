package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.Logestique;
import tn.example.charity.Repository.EventRepository;
import tn.example.charity.Repository.LogestiqueRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LogestiqueServiceImpl implements  ILogestiqueService{
    @Autowired
    private LogestiqueRepository logestiqueRepository;
    private EventRepository eventRepository;

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

    @Override
    public Logestique assignLogestiqueToEvent(Long idlogestique, Long eventId) {
        Logestique logestique = logestiqueRepository.findById(idlogestique)
                .orElseThrow(() -> new RuntimeException("Logestique not found"));

        tn.example.charity.Entity.Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        logestique.setEvent(event);
        return logestiqueRepository.save(logestique);
    }

    @Override
    public Logestique assignLogestiqueToEventWithQuantity(Long idlogestique, Long idEvent, float quantity) {
        Logestique log = logestiqueRepository.findById(idlogestique)
                .orElseThrow(() -> new RuntimeException("Logistique non trouvée"));

        Event event = eventRepository.findById(idEvent)
                .orElseThrow(() -> new RuntimeException("Event non trouvé"));

        if (log.getQuantity() < quantity) {
            throw new RuntimeException("Quantité insuffisante pour l’assignation");
        }

        // Mise à jour de la quantité restante
        log.setQuantity(log.getQuantity() - quantity);

        // 🔴 Lien logique entre la logistique et l’événement
        log.setEvent(event);

        return logestiqueRepository.save(log);
    }


}
