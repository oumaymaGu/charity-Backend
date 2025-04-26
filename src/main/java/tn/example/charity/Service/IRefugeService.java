package tn.example.charity.Service;

import tn.example.charity.Entity.Refuge;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;

public interface IRefugeService {
    Refuge addRefuge(Refuge refuge);
    List<Refuge> getAllRefuge();
    Refuge getRefugeById(Long idRfg);
    void deleteRefugeById(Long idRfg);
    Refuge updateRefugeById(Refuge refuge);
    Refuge getLastRefuge();

    // New method for handling image upload
    Refuge uploadRefugeImage(Long idRfg, MultipartFile imageFile) throws IOException;

    // Existing method
    Refuge affectRefugeToLogement(Long idRefuge, Long idLogement);


}