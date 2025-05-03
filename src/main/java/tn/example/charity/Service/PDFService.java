package tn.example.charity.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Livraisons;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class PDFService {

    @Autowired
    private ILivraisonService livraisonService;

    public byte[] genererRecuPDF(Long id) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Vérifie si la livraison est trouvée
        Livraisons livraison = livraisonService.retrieveallLivraisonbyid(id);
        if (livraison == null) {
            throw new Exception("Livraison non trouvée pour l'ID : " + id);
        }

        // Crée un writer pour le PDF
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Ajouter les informations de la livraison
        document.add(new Paragraph("Reçu de Livraison"));
        document.add(new Paragraph("Client : " + livraison.getNom()));
        document.add(new Paragraph("Adresse : " + livraison.getAdresseLivr()));
        document.add(new Paragraph("Date de Livraison : " + livraison.getDateLivraison()));
        document.add(new Paragraph("Email Client : " + livraison.getEmailClient()));
        document.add(new Paragraph("Code de Livraison : " + livraison.getPinCode()));

        // Vérification et ajout du logo
        String logoPath = "C:\\Users\\hadhe\\Downloads\\charity-Frontend-master\\charity-Frontend-master\\src\\assets\\img\\logo.jpg";
        if (Files.exists(Paths.get(logoPath))) {
            Image logo = new Image(ImageDataFactory.create(logoPath));
            logo.scaleToFit(100, 100);  // Ajuste la taille du logo
            logo.setFixedPosition(200, 750);  // Positionner le logo sur la page
            document.add(logo);
        } else {
            System.out.println("Le fichier logo n'a pas été trouvé à l'emplacement : " + logoPath);
        }

        // Ajouter l'image de signature si elle existe
        if (livraison.getSignatureImage() != null && !livraison.getSignatureImage().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(livraison.getSignatureImage());
            Image signature = new Image(ImageDataFactory.create(decodedBytes));
            signature.scaleToFit(150, 75);  // Ajuster la taille de la signature
            signature.setFixedPosition(200, 650);  // Positionner la signature sur la page
            document.add(signature);
        } else {
            System.out.println("Aucune signature trouvée pour la livraison ID : " + id);
        }

        // Fermer le document PDF
        document.close();

        // Retourner le contenu PDF en tant que tableau de bytes
        return byteArrayOutputStream.toByteArray();
    }
}
