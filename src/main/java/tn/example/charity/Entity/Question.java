package tn.example.charity.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    // Pour les questions à choix multiples
    @ElementCollection
    @Column(length = 500)
    private List<String> possibleAnswers;

    // Facteurs de risque associés à cette question
    private Boolean checksViolentThoughts;
    private Boolean checksParanoia;
    private Boolean checksImpulsivity;
    private Boolean checksSocialIsolation;
    private Boolean checksSubstanceAbuse;
    private boolean checksHallucinations;
    private Boolean checksHostility;

    // Pondération pour le calcul du score
    private Double weight;

    // Catégorie de la question
    @Enumerated(EnumType.STRING)
    private QuestionCategory category;
}