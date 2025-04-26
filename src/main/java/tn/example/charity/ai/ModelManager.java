package tn.example.charity.ai;

import lombok.Value;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.core.SerializationHelper;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Gestionnaire des modèles d'IA
 * Charge et gère les modèles d'apprentissage automatique
 */
@Component
public class ModelManager {


    private static final Logger logger = Logger.getLogger(ModelManager.class.getName());

    private Map<String, Classifier> models = new HashMap<>();
    private boolean modelsLoaded = false;

    @PostConstruct

    public void init() {
        try {
            createModelDirectories();
            loadModels();
            modelsLoaded = true;
        } catch (Exception e) {
            logger.severe("Erreur lors de l'initialisation des modèles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge les modèles depuis les fichiers
     */
    private void loadModels() throws Exception {
        // Créer les dossiers de modèles s'ils n'existent pas
        createModelDirectories();

        // Charger le modèle de classification de sentiment
        try {
            File sentimentModelFile = new File("src/main/resources/models/sentiment/sentiment_classifier.model");
            if (sentimentModelFile.exists()) {
                Classifier sentimentModel = (Classifier) SerializationHelper.read(sentimentModelFile.getAbsolutePath());
                models.put("sentiment", sentimentModel);
                logger.info("Modèle de sentiment chargé avec succès");
            } else {
                logger.info("Modèle de sentiment non trouvé, utilisation de l'approche basée sur des règles");
            }
        } catch (Exception e) {
            logger.warning("Erreur lors du chargement du modèle de sentiment: " + e.getMessage());
        }

        // Charger les modèles de régression pour les facteurs de risque
        String[] riskFactors = {"violentThoughts", "paranoia", "impulsivity", "socialIsolation", "substanceAbuse", "hostility"};

        for (String factor : riskFactors) {
            try {
                File riskModelFile = new File("src/main/resources/models/risk/" + factor + "_regressor.model");
                if (riskModelFile.exists()) {
                    Classifier riskModel = (Classifier) SerializationHelper.read(riskModelFile.getAbsolutePath());
                    models.put(factor, riskModel);
                    logger.info("Modèle de risque pour " + factor + " chargé avec succès");
                }
            } catch (Exception e) {
                logger.warning("Erreur lors du chargement du modèle pour " + factor + ": " + e.getMessage());
            }
        }
    }

    /**
     * Crée les répertoires nécessaires pour les modèles
     */
    private void createModelDirectories() {
        String[] directories = {
                "src/main/resources/models",
                "src/main/resources/models/sentiment",
                "src/main/resources/models/risk",
                "src/main/resources/models/nlp"
        };

        for (String dir : directories) {
            File directory = new File(dir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("Répertoire créé: " + dir);
                } else {
                    logger.warning("Impossible de créer le répertoire: " + dir);
                }
            }
        }
    }

    /**
     * Vérifie si les modèles sont chargés
     * @return true si au moins un modèle est chargé
     */
    public boolean areModelsLoaded() {
        return modelsLoaded && !models.isEmpty();
    }

    /**
     * Récupère un modèle par son nom
     * @param modelName Nom du modèle
     * @return Le modèle ou null s'il n'existe pas
     */
    public Classifier getModel(String modelName) {
        return models.get(modelName);
    }

    /**
     * Sauvegarde un modèle
     * @param modelName Nom du modèle
     * @param model Modèle à sauvegarder
     */
    public void saveModel(String modelName, Classifier model) throws Exception {
        if (modelName.startsWith("sentiment")) {
            SerializationHelper.write("src/main/resources/models/sentiment/" + modelName + ".model", model);
        } else {
            SerializationHelper.write("src/main/resources/models/risk/" + modelName + ".model", model);
        }

        // Mettre à jour le modèle en mémoire
        models.put(modelName, model);
    }
}