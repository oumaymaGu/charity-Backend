package tn.example.charity.dto;


public class BilletDTO {

    private String nomEvent;
    private String dateEvent;
    private String nomParticipant;
    private String qrCodeBase64;

    // Constructeurs
    public BilletDTO() {}

    public BilletDTO(String nomEvent, String dateEvent, String nomParticipant, String qrCodeBase64) {
        this.nomEvent = nomEvent;
        this.dateEvent = dateEvent;
        this.nomParticipant = nomParticipant;
        this.qrCodeBase64 = qrCodeBase64;
    }

    // Getters & Setters
    public String getNomEvent() {
        return nomEvent;
    }

    public void setNomEvent(String nomEvent) {
        this.nomEvent = nomEvent;
    }

    public String getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(String dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getNomParticipant() {
        return nomParticipant;
    }

    public void setNomParticipant(String nomParticipant) {
        this.nomParticipant = nomParticipant;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}
