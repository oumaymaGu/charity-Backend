package tn.example.charity.Service;

import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.dto.MedicationInfo;
import java.io.IOException;

public interface IOcrService {
    MedicationInfo extractMedicationInfo(MultipartFile file) throws IOException;
}