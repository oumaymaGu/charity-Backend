package tn.example.charity.dto;

import java.util.Date;

public class DonationDTO {

        private String donationType; // "MATERIEL" ou "ARGENT"
        private Double amount; // Pour les dons financiers
        private String itemName; // Pour les dons matériels
        private Integer quantity; // Pour les dons matériels
        private String donorName;
        private String message;
        private Date timestamp;
        // Getters/Setters
    }

