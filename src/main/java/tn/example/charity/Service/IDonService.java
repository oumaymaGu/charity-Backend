package tn.example.charity.Service;

import tn.example.charity.Entity.Don;

import java.util.List;
import java.util.Optional;

public interface IDonService {

        Don addDon(Don don);
        void deleteDon(Long idDon);
        Don modifyDon(Don don);
        List<Don> getAllDon();
        List<Don> retreiveallDon(Don don);
        Don retrieveallDonbyid(Long idDon);
        Optional<Don> findByImageHash(String imageHash);
}
