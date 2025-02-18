package tn.example.charity.Service;

import tn.example.charity.Entity.Associations;

import java.util.List;

public interface IAssociationService {
    Associations addAssociations(Associations associations);
    Associations updateAssociations(Associations associations);
    void deleteAssociations(Long idAss);
    Associations getAssociationsById(Long idAss);
    List<Associations> getAllAssociations();

}
