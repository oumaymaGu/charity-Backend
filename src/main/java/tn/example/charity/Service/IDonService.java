package tn.example.charity.Service;

import tn.example.charity.Entity.Don;

import java.util.List;

public interface IDonService {

        Don addDon(Don don);
        void deleteDon(Long idDon);
        Don modifyDon(Don don);
        List<Don> getAllDon();
        List<Don> retreiveallDon(Don don);
        Don retrieveallDonbyid(Long idDon);
}
