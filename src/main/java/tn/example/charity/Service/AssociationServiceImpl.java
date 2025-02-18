package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Repository.AssociationsRepository;

import java.util.List;
@Service
@AllArgsConstructor
public class AssociationServiceImpl implements IAssociationService {

    AssociationsRepository associationsRepository;


    public Associations addAssociations(Associations associations) {
        return associationsRepository.save(associations);
    }

    public Associations updateAssociations(Associations associations) {
        return associationsRepository.save(associations);
    }

    public void  deleteAssociations(Long idAss) {
        associationsRepository.deleteById(idAss);
    }


    public Associations getAssociationsById(Long idAss) {
        return associationsRepository.findById(idAss).get();
    }

    public List<Associations> getAllAssociations() {
        List<Associations> associationsList = associationsRepository.findAll();
        return associationsList;
    }




}
