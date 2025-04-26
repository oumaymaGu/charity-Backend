package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.PsychologicalEvaluation;
import tn.example.charity.Entity.RiskLevel;

import java.util.List;

@Repository
public interface PsychologicalEvaluationRepository extends JpaRepository<PsychologicalEvaluation, Long> {
    List<PsychologicalEvaluation> findByRefugeIdRfg(Long idRfg);
    List<PsychologicalEvaluation> findByRiskLevel(RiskLevel riskLevel);
    List<PsychologicalEvaluation> findByRequiresImmediateAttentionTrue();
}
