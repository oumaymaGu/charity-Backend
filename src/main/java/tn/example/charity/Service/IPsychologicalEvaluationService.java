package tn.example.charity.Service;

import tn.example.charity.Entity.*;

import java.util.List;

public interface IPsychologicalEvaluationService {
    // Gestion des évaluations
    PsychologicalEvaluation startEvaluation(Long idRfg);
    PsychologicalEvaluation getEvaluationById(Long evaluationId);
    List<PsychologicalEvaluation> getEvaluationsByRefuge(Long idRfg);
    PsychologicalEvaluation completeEvaluation(Long evaluationId);

    // Gestion des réponses
    QuestionResponse saveResponse(Long evaluationId, Long questionId, String answer);
    List<QuestionResponse> getResponsesByEvaluation(Long evaluationId);

    // Recommandations
    List<SupportService> getRecommendedServices(Long evaluationId);

    // Reporting
    List<PsychologicalEvaluation> getHighRiskEvaluations();
    List<PsychologicalEvaluation> getEvaluationsRequiringImmediateAttention();
}