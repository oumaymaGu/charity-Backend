package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.*;
import tn.example.charity.Repository.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PsychologicalEvaluationServiceImpl implements IPsychologicalEvaluationService {

    private final PsychologicalEvaluationRepository evaluationRepository;
    private final QuestionRepository questionRepository;
    private final QuestionResponseRepository responseRepository;
    private final SupportServiceRepository serviceRepository;
    private final RefugeRepository refugeRepository;

    @Override
    @Transactional
    public PsychologicalEvaluation startEvaluation(Long idRfg) {
        Refuge refuge = refugeRepository.findById(idRfg)
                .orElseThrow(() -> new RuntimeException("Refuge not found with id: " + idRfg));

        PsychologicalEvaluation evaluation = new PsychologicalEvaluation();
        evaluation.setRefuge(refuge);
        evaluation.setEvaluationDate(LocalDateTime.now());
        evaluation.setStatus(EvaluationStatus.IN_PROGRESS);
        evaluation.setRequiresImmediateAttention(false);

        // Initialiser les scores à zéro
        evaluation.setViolentThoughtsScore(0.0);
        evaluation.setParanoiaScore(0.0);
        evaluation.setImpulsivityScore(0.0);
        evaluation.setSocialIsolationScore(0.0);
        evaluation.setSubstanceAbuseScore(0.0);
        evaluation.setHostilityScore(0.0);
        evaluation.setOverallRiskScore(0.0);
        evaluation.setRiskLevel(RiskLevel.LOW);

        return evaluationRepository.save(evaluation);
    }

    @Override
    public PsychologicalEvaluation getEvaluationById(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation not found with id: " + evaluationId));
    }

    @Override
    public List<PsychologicalEvaluation> getEvaluationsByRefuge(Long idRfg) {
        return evaluationRepository.findByRefugeIdRfg(idRfg);
    }

    @Override
    @Transactional
    public PsychologicalEvaluation completeEvaluation(Long evaluationId) {
        PsychologicalEvaluation evaluation = getEvaluationById(evaluationId);

        // Calculer les scores finaux
        calculateFinalScores(evaluation);

        // Déterminer les services recommandés
        List<SupportService> recommendedServices = findRecommendedServices(evaluation);
        evaluation.setRecommendedServices(recommendedServices);

        // Finaliser l'évaluation
        evaluation.setStatus(EvaluationStatus.COMPLETED);

        return evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public QuestionResponse saveResponse(Long evaluationId, Long questionId, String answer) {
        PsychologicalEvaluation evaluation = getEvaluationById(evaluationId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        // Créer et sauvegarder la réponse
        QuestionResponse response = new QuestionResponse();
        response.setEvaluation(evaluation);
        response.setQuestion(question);
        response.setResponse(answer);

        // Calculer le score pour cette réponse
        calculateResponseScore(response);

        // Ajouter à l'évaluation
        if (evaluation.getResponses() == null) {
            evaluation.setResponses(new ArrayList<>());
        }
        evaluation.getResponses().add(response);

        // Mettre à jour les scores de l'évaluation
        updateEvaluationScores(evaluation);

        // Sauvegarder la réponse
        return responseRepository.save(response);
    }

    @Override
    public List<QuestionResponse> getResponsesByEvaluation(Long evaluationId) {
        return responseRepository.findByEvaluationId(evaluationId);
    }

    @Override
    public List<SupportService> getRecommendedServices(Long evaluationId) {
        PsychologicalEvaluation evaluation = getEvaluationById(evaluationId);
        return evaluation.getRecommendedServices();
    }

    @Override
    public List<PsychologicalEvaluation> getHighRiskEvaluations() {
        List<PsychologicalEvaluation> highRisk = evaluationRepository.findByRiskLevel(RiskLevel.HIGH);
        List<PsychologicalEvaluation> severeRisk = evaluationRepository.findByRiskLevel(RiskLevel.SEVERE);

        List<PsychologicalEvaluation> result = new ArrayList<>(highRisk);
        result.addAll(severeRisk);

        return result;
    }

    @Override
    public List<PsychologicalEvaluation> getEvaluationsRequiringImmediateAttention() {
        return evaluationRepository.findByRequiresImmediateAttentionTrue();
    }

    private void calculateResponseScore(QuestionResponse response) {
        Question question = response.getQuestion();
        String answer = response.getResponse();
        double baseScore = 0.0;

        switch (question.getType()) {
            case YES_NO:
                if ("yes".equalsIgnoreCase(answer)) {
                    baseScore = 1.0;
                }
                break;
            case SCALE:
                try {
                    int value = Integer.parseInt(answer);
                    baseScore = value / 10.0;
                } catch (NumberFormatException e) {
                    baseScore = 0.0;
                }
                break;
            case MULTIPLE_CHOICE:
                int index = question.getPossibleAnswers().indexOf(answer);
                if (index >= 0) {
                    baseScore = index / (double)(question.getPossibleAnswers().size() - 1);
                }
                break;
            case TEXT:
                baseScore = 0.5;
                break;
        }

        double weightedScore = baseScore * question.getWeight();
        response.setScore(weightedScore);

        if (question.getChecksViolentThoughts()) {
            response.setViolentThoughtsContribution(weightedScore);
        }
        if (question.getChecksParanoia()) {
            response.setParanoiaContribution(weightedScore);
        }
        if (question.getChecksImpulsivity()) {
            response.setImpulsivityContribution(weightedScore);
        }
        if (question.getChecksSocialIsolation()) {
            response.setSocialIsolationContribution(weightedScore);
        }
        if (question.getChecksSubstanceAbuse()) {
            response.setSubstanceAbuseContribution(weightedScore);
        }
        if (question.getChecksHostility()) {
            response.setHostilityContribution(weightedScore);
        }
    }

    private void updateEvaluationScores(PsychologicalEvaluation evaluation) {
        Map<String, List<Double>> factorScores = new HashMap<>();
        factorScores.put("violentThoughts", new ArrayList<>());
        factorScores.put("paranoia", new ArrayList<>());
        factorScores.put("impulsivity", new ArrayList<>());
        factorScores.put("socialIsolation", new ArrayList<>());
        factorScores.put("substanceAbuse", new ArrayList<>());
        factorScores.put("hostility", new ArrayList<>());

        for (QuestionResponse response : evaluation.getResponses()) {
            if (response.getViolentThoughtsContribution() != null && response.getViolentThoughtsContribution() > 0) {
                factorScores.get("violentThoughts").add(response.getViolentThoughtsContribution());
            }
            if (response.getParanoiaContribution() != null && response.getParanoiaContribution() > 0) {
                factorScores.get("paranoia").add(response.getParanoiaContribution());
            }
            if (response.getImpulsivityContribution() != null && response.getImpulsivityContribution() > 0) {
                factorScores.get("impulsivity").add(response.getImpulsivityContribution());
            }
            if (response.getSocialIsolationContribution() != null && response.getSocialIsolationContribution() > 0) {
                factorScores.get("socialIsolation").add(response.getSocialIsolationContribution());
            }
            if (response.getSubstanceAbuseContribution() != null && response.getSubstanceAbuseContribution() > 0) {
                factorScores.get("substanceAbuse").add(response.getSubstanceAbuseContribution());
            }
            if (response.getHostilityContribution() != null && response.getHostilityContribution() > 0) {
                factorScores.get("hostility").add(response.getHostilityContribution());
            }
        }

        evaluation.setViolentThoughtsScore(calculateAverage(factorScores.get("violentThoughts")));
        evaluation.setParanoiaScore(calculateAverage(factorScores.get("paranoia")));
        evaluation.setImpulsivityScore(calculateAverage(factorScores.get("impulsivity")));
        evaluation.setSocialIsolationScore(calculateAverage(factorScores.get("socialIsolation")));
        evaluation.setSubstanceAbuseScore(calculateAverage(factorScores.get("substanceAbuse")));
        evaluation.setHostilityScore(calculateAverage(factorScores.get("hostility")));

        double sum = evaluation.getViolentThoughtsScore() + evaluation.getParanoiaScore() +
                evaluation.getImpulsivityScore() + evaluation.getSocialIsolationScore() +
                evaluation.getSubstanceAbuseScore() + evaluation.getHostilityScore();

        double overallScore = sum / 6.0;
        evaluation.setOverallRiskScore(overallScore);

        if (overallScore < 0.3) {
            evaluation.setRiskLevel(RiskLevel.LOW);
        } else if (overallScore < 0.6) {
            evaluation.setRiskLevel(RiskLevel.MODERATE);
        } else if (overallScore < 0.8) {
            evaluation.setRiskLevel(RiskLevel.HIGH);
        } else {
            evaluation.setRiskLevel(RiskLevel.SEVERE);
        }

        boolean requiresImmediate = evaluation.getRiskLevel() == RiskLevel.SEVERE ||
                evaluation.getViolentThoughtsScore() > 0.8 ||
                evaluation.getHostilityScore() > 0.8;

        evaluation.setRequiresImmediateAttention(requiresImmediate);
    }

    private double calculateAverage(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private void calculateFinalScores(PsychologicalEvaluation evaluation) {
        updateEvaluationScores(evaluation);
    }

    private List<SupportService> findRecommendedServices(PsychologicalEvaluation evaluation) {
        List<SupportService> allServices = serviceRepository.findAll();
        List<SupportService> recommended = new ArrayList<>();

        if (evaluation.getRiskLevel() == RiskLevel.SEVERE) {
            recommended.addAll(serviceRepository.findByEmergencyServiceTrue());
        }

        if (evaluation.getViolentThoughtsScore() > 0.5) {
            recommended.addAll(serviceRepository.findBySpecializesInViolenceTrue());
        }

        if (evaluation.getParanoiaScore() > 0.5) {
            recommended.addAll(serviceRepository.findBySpecializesInParanoiaTrue());
        }

        return recommended.stream().distinct().collect(Collectors.toList());
    }
}
