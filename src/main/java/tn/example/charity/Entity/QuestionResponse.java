package tn.example.charity.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PsychologicalEvaluation evaluation;

    @ManyToOne
    private Question question;

    @Column(length = 2000)
    private String response;

    // Score calculé pour cette réponse
    private Double score;

    // Contributions aux différents facteurs de risque
    private Double violentThoughtsContribution;
    private Double paranoiaContribution;
    private Double impulsivityContribution;
    private Double socialIsolationContribution;
    private Double substanceAbuseContribution;
    private Double hostilityContribution;
}