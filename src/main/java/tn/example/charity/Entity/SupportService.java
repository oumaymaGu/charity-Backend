package tn.example.charity.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ServiceType type;

    private Boolean emergencyService;

    private String contactPhone;
    private String contactEmail;
    private String address;

    private Boolean available24h;

    // Spécialisations
    private Boolean specializesInViolence;
    private Boolean specializesInParanoia;
    private Boolean specializesInImpulsivity;
    private Boolean specializesInSocialSupport;
    private Boolean specializesInSubstanceAbuse;
    private Boolean specializesInAngerManagement;
}