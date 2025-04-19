package tn.example.charity.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity  // Cette annotation indique que la classe représente une entité dans une base de données (si tu veux sauvegarder ces données)
public class Location {

    @Id
    private Long id;  // Optionnel, si tu souhaites une clé primaire unique (cela peut être un identifiant généré automatiquement)

    private double latitude;  // Latitude de l'objet
    private double longitude;  // Longitude de l'objet

    // Constructeur sans argument (nécessaire pour les frameworks comme Spring Data JPA)
    public Location() {}

    // Constructeur avec des arguments pour initialiser la latitude et la longitude
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters pour les variables
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Optionnel : méthode toString() pour afficher la localisation facilement
    @Override
    public String toString() {
        return "Location{latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}