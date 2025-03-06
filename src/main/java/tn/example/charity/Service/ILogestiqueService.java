package tn.example.charity.Service;

import tn.example.charity.Entity.Logestique;

import java.util.List;

public interface ILogestiqueService {
    Logestique addlogestique(Logestique logestique);
    void deleteLogestique(Long  idlogestique);
    Logestique modifylog(Logestique logestique);
    List<Logestique> getAlllogs();
    Logestique retrievelogbyid(Long idlogestique);
    List<Logestique> retrievelogbyname(String name);

}
