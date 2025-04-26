package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.ai.ModelManager;
import tn.example.charity.ai.ModelTrainer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour l'entraînement et la gestion des modèles d'IA
 */
@RestController
@RequestMapping("/ai-model")
@CrossOrigin(origins = "http://localhost:4200")
public class ModelTrainingController {

    @Autowired
    private ModelTrainer modelTrainer;

    @Autowired
    private ModelManager modelManager;

    @Value("${ai.models.base-path:src/main/resources/models}")
    private String modelsBasePath;

    /**
     * Endpoint pour entraîner les modèles d'IA
     * @return Résultat de l'entraînement
     */
    @PostMapping("/train")
    public ResponseEntity<Map<String, Object>> trainModels() {
        try {
            Map<String, Object> result = modelTrainer.trainModels();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false,
                            "message", "Erreur lors de l'entraînement des modèles: " + e.getMessage()
                    )
            );
        }
    }

    /**
     * Endpoint pour vérifier l'état des modèles
     * @return Informations sur les modèles disponibles
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getModelStatus() {
        Map<String, Object> status = new HashMap<>();

        // Vérifier si les modèles sont chargés
        boolean modelsLoaded = modelManager.areModelsLoaded();
        status.put("modelsLoaded", modelsLoaded);

        // Vérifier quels modèles existent
        File sentimentModelFile = new File(modelsBasePath + "/sentiment/sentiment_classifier.model");
        status.put("sentimentModelExists", sentimentModelFile.exists());

        // Vérifier les modèles de risque
        Map<String, Boolean> riskModels = new HashMap<>();
        String[] riskFactors = {"violentThoughts", "paranoia", "impulsivity", "socialIsolation", "substanceAbuse", "hostility"};
        for (String factor : riskFactors) {
            File riskModelFile = new File(modelsBasePath + "/risk/" + factor + "_regressor.model");
            riskModels.put(factor, riskModelFile.exists());
        }
        status.put("riskModels", riskModels);

        return ResponseEntity.ok(status);
    }

    /**
     * Endpoint pour réinitialiser les modèles (suppression)
     * @return Résultat de la réinitialisation
     */
    @DeleteMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetModels() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Supprimer les fichiers de modèles
            File sentimentDir = new File(modelsBasePath + "/sentiment");
            File riskDir = new File(modelsBasePath + "/risk");

            int deletedCount = 0;

            if (sentimentDir.exists()) {
                for (File file : sentimentDir.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".model")) {
                        file.delete();
                        deletedCount++;
                    }
                }
            }

            if (riskDir.exists()) {
                for (File file : riskDir.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".model")) {
                        file.delete();
                        deletedCount++;
                    }
                }
            }

            result.put("success", true);
            result.put("message", "Modèles réinitialisés avec succès. " + deletedCount + " fichiers supprimés.");

            // Recharger les modèles (ou plutôt, constater qu'ils n'existent plus)
            modelManager.init();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur lors de la réinitialisation des modèles: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}