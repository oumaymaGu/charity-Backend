package tn.example.charity.dto;

import lombok.Data;
import tn.example.charity.Entity.Don;

@Data
public class DonWithMedicationDTO {
    private Don don;
    private MedicationInfo medicationInfo;
}
