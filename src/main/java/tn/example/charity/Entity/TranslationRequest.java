package tn.example.charity.Entity;

public class TranslationRequest {
    private String text;
    private String targetLang;

    // Constructor, getters and setters

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }
}

