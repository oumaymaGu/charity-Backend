package tn.example.charity.dto;

import lombok.Data;

@Data
public class MedicationInfo {
    private String medicationName;
    private String expirationDate;
    private String lotNumber;
    private String productCode; // Ajout de ce champ
    private String rawText; // Pour le débogage

    // Constructeur mis à jour
    public MedicationInfo(String medicationName, String expirationDate, String lotNumber, String productCode, String rawText) {
        this.medicationName = medicationName;
        this.expirationDate = expirationDate;
        this.lotNumber = lotNumber;
        this.productCode = productCode;
        this.rawText = rawText;
    }
}