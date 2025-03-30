package tn.example.charity.Service;

import tn.example.charity.Entity.Refuge;

import java.util.List;

public interface IRefugeService {

    Refuge addRefuge(Refuge refuge);
    List<Refuge> getAllRefuge();
    Refuge getRefugeById(Long idRfg);
    void deleteRefugeById(Long idRfg);
    Refuge updateRefugeById(Refuge refuge);


}
