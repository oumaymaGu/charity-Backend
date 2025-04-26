package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PsychologicalEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "refuge_id_rfg") // Add this line
    private Refuge refuge;

    private LocalDateTime evaluationDate;

    // Scores pour différents facteurs de risque (0.0 à 1.0)
    private Double violentThoughtsScore;
    private Double paranoiaScore;
    private Double impulsivityScore;
    private Double socialIsolationScore;
    private Double substanceAbuseScore;
    private Double hostilityScore;

    // Score global de risque
    private Double overallRiskScore;

    // Niveau de risque calculé
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    // Statut de l'évaluation
    @Enumerated(EnumType.STRING)
    private EvaluationStatus status;

    // Notes supplémentaires
    @Column(length = 2000)
    private String evaluatorNotes;

    // Indicateur d'urgence
    private Boolean requiresImmediateAttention;

    // Relations avec les réponses
    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL)
    private List<QuestionResponse> responses;

    // Services recommandés
    @ManyToMany
    private List<SupportService> recommendedServices;
}
