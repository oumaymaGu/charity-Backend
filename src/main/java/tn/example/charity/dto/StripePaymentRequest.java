package tn.example.charity.dto;

import lombok.*;
import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentRequest {
    @Positive(message = "Le montant doit être positif")
    @NotNull(message = "Le montant ne peut pas être nul")
    @Max(value = 1000, message = "Le montant semble être en centimes, veuillez entrer le montant en euros") // Ajout d'une validation
    private Double amount;

    @NotBlank(message = "La devise ne peut pas être vide")
    @Pattern(regexp = "^[A-Z]{3}$", message = "La devise doit être au format ISO 3 lettres majuscules")
    private String currency;

    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être une adresse valide")
    private String email;

    @NotBlank(message = "La description ne peut pas être vide")
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    private Long donId;

    public long getAmountInCents() {
        return (long) (this.amount * 100);
    }
}