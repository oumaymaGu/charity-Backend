package tn.example.charity.Service;

import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.SuiviDon;

import java.util.List;

public interface ISuiviDonService {
    SuiviDon addSuiviDon(SuiviDon suiviDon);
    void deleteSuiviDon(Long idSuivi);
    SuiviDon modifySuiviDon(SuiviDon suiviDon);
    List<SuiviDon> getAllSuiviDon();
    List<SuiviDon> retreiveallSuiviDon(SuiviDon suiviDon);
    SuiviDon retrieveallSuiviDonbyid(Long idSuivi);
}
