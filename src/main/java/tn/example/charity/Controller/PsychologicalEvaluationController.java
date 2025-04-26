package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.*;
import tn.example.charity.Service.IPsychologicalEvaluationService;
import tn.example.charity.Repository.QuestionRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/psychological-evaluation")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class PsychologicalEvaluationController {

    private final IPsychologicalEvaluationService evaluationService;
    private final QuestionRepository questionRepository;

    @PostMapping("/start/{refugeId}")
    public ResponseEntity<PsychologicalEvaluation> startEvaluation(@PathVariable Long refugeId) {
        try {
            PsychologicalEvaluation evaluation = evaluationService.startEvaluation(refugeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(evaluation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{evaluationId}")
    public ResponseEntity<PsychologicalEvaluation> getEvaluation(@PathVariable Long evaluationId) {
        try {
            PsychologicalEvaluation evaluation = evaluationService.getEvaluationById(evaluationId);
            return ResponseEntity.ok(evaluation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/refuge/{refugeId}")
    public ResponseEntity<List<PsychologicalEvaluation>> getEvaluationsByRefuge(@PathVariable Long refugeId) {
        List<PsychologicalEvaluation> evaluations = evaluationService.getEvaluationsByRefuge(refugeId);
        return ResponseEntity.ok(evaluations);
    }

    @PostMapping("/{evaluationId}/answer")
    public ResponseEntity<QuestionResponse> saveResponse(
            @PathVariable Long evaluationId,
            @RequestBody Map<String, Object> requestBody) {

        try {
            Long questionId = Long.parseLong(requestBody.get("questionId").toString());
            String answer = requestBody.get("answer").toString();

            QuestionResponse response = evaluationService.saveResponse(evaluationId, questionId, answer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{evaluationId}/complete")
    public ResponseEntity<PsychologicalEvaluation> completeEvaluation(@PathVariable Long evaluationId) {
        try {
            PsychologicalEvaluation evaluation = evaluationService.completeEvaluation(evaluationId);
            return ResponseEntity.ok(evaluation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{evaluationId}/recommendations")
    public ResponseEntity<List<SupportService>> getRecommendations(@PathVariable Long evaluationId) {
        try {
            List<SupportService> services = evaluationService.getRecommendedServices(evaluationId);
            return ResponseEntity.ok(services);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/questions/category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        try {
            QuestionCategory questionCategory = QuestionCategory.valueOf(category.toUpperCase());
            List<Question> questions = questionRepository.findByCategory(questionCategory);
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/high-risk")
    public ResponseEntity<List<PsychologicalEvaluation>> getHighRiskEvaluations() {
        List<PsychologicalEvaluation> evaluations = evaluationService.getHighRiskEvaluations();
        return ResponseEntity.ok(evaluations);
    }

    @GetMapping("/immediate-attention")
    public ResponseEntity<List<PsychologicalEvaluation>> getEvaluationsRequiringImmediateAttention() {
        List<PsychologicalEvaluation> evaluations = evaluationService.getEvaluationsRequiringImmediateAttention();
        return ResponseEntity.ok(evaluations);
    }
}
