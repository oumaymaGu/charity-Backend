package tn.example.charity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Reservation;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReservationNotification(Reservation reservation, String adminEmail) {
        // Créer un message mime au lieu d'un SimpleMailMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(adminEmail);
            helper.setTo(reservation.getDemandeur().getEmail());
            helper.setSubject("Reservation Status Update");

            // Contenu HTML de l'email avec CSS intégré
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            line-height: 1.6;\n" +
                    "            color: #333;\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 0 auto;\n" +
                    "        }\n" +
                    "        .header {\n" +
                    "            background-color: #4A90E2;\n" +
                    "            color: white;\n" +
                    "            padding: 20px;\n" +
                    "            text-align: center;\n" +
                    "            border-radius: 5px 5px 0 0;\n" +
                    "        }\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "            border: 1px solid #ddd;\n" +
                    "        }\n" +
                    "        .details {\n" +
                    "            background-color: white;\n" +
                    "            padding: 15px;\n" +
                    "            margin: 15px 0;\n" +
                    "            border-radius: 4px;\n" +
                    "            border-left: 4px solid #4A90E2;\n" +
                    "        }\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #777;\n" +
                    "        }\n" +
                    "        .status {\n" +
                    "            font-weight: bold;\n" +
                    "            color: #4A90E2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class='header'>\n" +
                    "        <h2>Notification de Réservation</h2>\n" +
                    "    </div>\n" +
                    "    <div class='content'>\n" +
                    "        <p>Bonjour <b>" + reservation.getDemandeur().getUsername() + "</b>,</p>\n" +
                    "        <p>Votre réservation pour <b>" + reservation.getLogement().getNom() + "</b> a été <span class='status'>" +
                    reservation.getStatut().toLowerCase() + "</span>.</p>\n" +
                    "        <div class='details'>\n" +
                    "            <h3>Détails de la réservation:</h3>\n" +
                    "            <p><strong>Logement:</strong> " + reservation.getLogement().getNom() + "</p>\n" +
                    "            <p><strong>Adresse:</strong> " + reservation.getLogement().getAdresse() + "</p>\n" +
                    "            <p><strong>Date de demande:</strong> " + reservation.getDateDemande() + "</p>\n" +
                    "        </div>\n" +
                    "        <p>Merci de votre confiance,</p>\n" +
                    "        <p>L'équipe Charity</p>\n" +
                    "    </div>\n" +
                    "    <div class='footer'>\n" +
                    "        <p>Ce message est envoyé automatiquement, merci de ne pas y répondre.</p>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            // Définir le contenu HTML
            helper.setText(htmlContent, true);

            // Envoyer l'email
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    /**
     * Sends a password reset email with a link to reset the password
     *
     * @param to The recipient's email address
     * @param resetLink The password reset link
     */
    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n\n" + resetLink +
                "\n\nIf you did not request a password reset, please ignore this email.");

        mailSender.send(message);
    }

    /**
     * Sends a password reset email with HTML formatting
     *
     * @param to The recipient's email address
     * @param resetLink The password reset link
     */
    public void sendPasswordResetEmailHtml(String to, String resetLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Password Reset Request");

            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            line-height: 1.6;\n" +
                    "            color: #333;\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 0 auto;\n" +
                    "        }\n" +
                    "        .header {\n" +
                    "            background-color: #4A90E2;\n" +
                    "            color: white;\n" +
                    "            padding: 20px;\n" +
                    "            text-align: center;\n" +
                    "            border-radius: 5px 5px 0 0;\n" +
                    "        }\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "            border: 1px solid #ddd;\n" +
                    "        }\n" +
                    "        .button {\n" +
                    "            display: inline-block;\n" +
                    "            background-color: #4A90E2;\n" +
                    "            color: white;\n" +
                    "            padding: 12px 24px;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 4px;\n" +
                    "            margin: 20px 0;\n" +
                    "        }\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #777;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class='header'>\n" +
                    "        <h2>Réinitialisation de Mot de Passe</h2>\n" +
                    "    </div>\n" +
                    "    <div class='content'>\n" +
                    "        <p>Bonjour,</p>\n" +
                    "        <p>Nous avons reçu une demande de réinitialisation de mot de passe pour votre compte.</p>\n" +
                    "        <p>Si vous n'avez pas fait cette demande, veuillez ignorer cet email.</p>\n" +
                    "        <p>Pour réinitialiser votre mot de passe, veuillez cliquer sur le bouton ci-dessous:</p>\n" +
                    "        <p style='text-align: center;'>\n" +
                    "            <a href='" + resetLink + "' class='button'>Réinitialiser mon mot de passe</a>\n" +
                    "        </p>\n" +
                    "        <p>Si le bouton ne fonctionne pas, copiez et collez le lien suivant dans votre navigateur:</p>\n" +
                    "        <p>" + resetLink + "</p>\n" +
                    "        <p>Ce lien expirera dans 24 heures.</p>\n" +
                    "        <p>Cordialement,</p>\n" +
                    "        <p>L'équipe Charity</p>\n" +
                    "    </div>\n" +
                    "    <div class='footer'>\n" +
                    "        <p>Ce message est envoyé automatiquement, merci de ne pas y répondre.</p>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}