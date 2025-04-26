package tn.example.charity.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.example.charity.Entity.PsychologicalEvaluation;
import tn.example.charity.Entity.QuestionResponse;
import tn.example.charity.Entity.QuestionType;
import tn.example.charity.Repository.PsychologicalEvaluationRepository;
import tn.example.charity.Repository.QuestionResponseRepository;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Classe responsable de l'entraînement des modèles d'IA
 */
@Component
public class ModelTrainer {

    private static final Logger logger = Logger.getLogger(ModelTrainer.class.getName());

    @Autowired
    private QuestionResponseRepository responseRepository;

    @Autowired
    private PsychologicalEvaluationRepository evaluationRepository;

    @Autowired
    private TextPreprocessor textPreprocessor;

    @Autowired
    private ModelManager modelManager;

    /**
     * Entraîne les modèles d'IA à partir des données existantes
     * @return Résumé de l'entraînement
     */
    public Map<String, Object> trainModels() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Collecter les données d'entraînement
            List<TrainingData> trainingData = collectTrainingData();

            if (trainingData.isEmpty()) {
                logger.warning("Pas assez de données pour entraîner les modèles");
                result.put("success", false);
                result.put("message", "Pas assez de données pour entraîner les modèles");
                return result;
            }

            result.put("dataSize", trainingData.size());

            // Entraîner le modèle de sentiment
            Map<String, Object> sentimentResults = trainSentimentModel(trainingData);
            result.put("sentimentModel", sentimentResults);

            // Entraîner les modèles de risque
            Map<String, Object> riskResults = trainRiskModels(trainingData);
            result.put("riskModels", riskResults);

            logger.info("Entraînement des modèles terminé avec succès");
            result.put("success", true);
            result.put("message", "Entraînement des modèles terminé avec succès");

        } catch (Exception e) {
            logger.severe("Erreur lors de l'entraînement des modèles: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Erreur lors de l'entraînement des modèles: " + e.getMessage());
        }

        return result;
    }

    /**
     * Collecte les données d'entraînement à partir des réponses textuelles
     */
    private List<TrainingData> collectTrainingData() {
        List<TrainingData> trainingData = new ArrayList<>();

        // Récupérer toutes les réponses textuelles
        List<QuestionResponse> textResponses = responseRepository.findAll();

        for (QuestionResponse response : textResponses) {
            // Ne prendre que les réponses textuelles
            if (response.getQuestion() == null || response.getQuestion().getType() != QuestionType.TEXT) {
                continue;
            }

            PsychologicalEvaluation evaluation = response.getEvaluation();

            if (evaluation != null) {
                TrainingData data = new TrainingData();
                data.setText(response.getResponse());

                // Déterminer le sentiment
                if (evaluation.getOverallRiskScore() > 0.7) {
                    data.setSentiment("très négatif");
                } else if (evaluation.getOverallRiskScore() > 0.5) {
                    data.setSentiment("négatif");
                } else if (evaluation.getOverallRiskScore() > 0.3) {
                    data.setSentiment("légèrement négatif");
                } else {
                    data.setSentiment("neutre");
                }

                // Ajouter les scores de risque
                Map<String, Double> riskScores = new HashMap<>();
                riskScores.put("violentThoughts", evaluation.getViolentThoughtsScore());
                riskScores.put("paranoia", evaluation.getParanoiaScore());
                riskScores.put("impulsivity", evaluation.getImpulsivityScore());
                riskScores.put("socialIsolation", evaluation.getSocialIsolationScore());
                riskScores.put("substanceAbuse", evaluation.getSubstanceAbuseScore());
                riskScores.put("hostility", evaluation.getHostilityScore());

                data.setRiskScores(riskScores);
                trainingData.add(data);
            }
        }

        return trainingData;
    }

    /**
     * Entraîne le modèle de classification de sentiment
     */
    private Map<String, Object> trainSentimentModel(List<TrainingData> trainingData) throws Exception {
        Map<String, Object> results = new HashMap<>();

        // Créer les attributs pour le dataset Weka
        ArrayList<Attribute> attributes = new ArrayList<>();

        // Attribut pour le texte
        attributes.add(new Attribute("text", (ArrayList<String>) null));

        // Attribut pour le sentiment
        ArrayList<String> sentimentValues = new ArrayList<>();
        sentimentValues.add("neutre");
        sentimentValues.add("légèrement négatif");
        sentimentValues.add("négatif");
        sentimentValues.add("très négatif");
        attributes.add(new Attribute("sentiment", sentimentValues));

        // Créer le dataset
        Instances dataset = new Instances("SentimentAnalysis", attributes, trainingData.size());
        dataset.setClassIndex(1);

        // Ajouter les instances
        for (TrainingData data : trainingData) {
            DenseInstance instance = new DenseInstance(2);
            instance.setValue(attributes.get(0), textPreprocessor.preprocess(data.getText()));
            instance.setValue(attributes.get(1), data.getSentiment());
            dataset.add(instance);
        }

        // Appliquer le filtre StringToWordVector
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(dataset);
        filter.setIDFTransform(true);
        filter.setTFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);

        Instances filteredData = Filter.useFilter(dataset, filter);

        // Sauvegarder le dataset au format ARFF pour référence
        File arffDir = new File("src/main/resources/models/sentiment");
        if (!arffDir.exists()) {
            arffDir.mkdirs();
        }

        ArffSaver saver = new ArffSaver();
        saver.setInstances(filteredData);
        saver.setFile(new File("src/main/resources/models/sentiment/sentiment_dataset.arff"));
        saver.writeBatch();

        // Entraîner le modèle RandomForest
        RandomForest classifier = new RandomForest();
        classifier.setNumIterations(100);
        classifier.buildClassifier(filteredData);

        // Évaluer le modèle
        Evaluation evaluation = new Evaluation(filteredData);
        evaluation.crossValidateModel(classifier, filteredData, 10, new java.util.Random(1));

        logger.info("=== Évaluation du modèle de sentiment ===");
        logger.info(evaluation.toSummaryString());

        // Sauvegarder le modèle
        modelManager.saveModel("sentiment_classifier", classifier);

        // Résultats
        results.put("accuracy", evaluation.pctCorrect());
        results.put("instances", dataset.numInstances());
        results.put("summary", evaluation.toSummaryString());

        return results;
    }

    /**
     * Entraîne les modèles de régression pour les scores de risque
     */
    private Map<String, Object> trainRiskModels(List<TrainingData> trainingData) throws Exception {
        Map<String, Object> results = new HashMap<>();
        String[] riskFactors = {"violentThoughts", "paranoia", "impulsivity", "socialIsolation", "substanceAbuse", "hostility"};

        for (String factor : riskFactors) {
            // Créer les attributs pour le dataset Weka
            ArrayList<Attribute> attributes = new ArrayList<>();

            // Attribut pour le texte
            attributes.add(new Attribute("text", (ArrayList<String>) null));

            // Attribut pour le score de risque
            attributes.add(new Attribute("score"));

            // Créer le dataset
            Instances dataset = new Instances(factor + "Analysis", attributes, trainingData.size());
            dataset.setClassIndex(1);

            // Ajouter les instances
            for (TrainingData data : trainingData) {
                DenseInstance instance = new DenseInstance(2);
                instance.setValue(attributes.get(0), textPreprocessor.preprocess(data.getText()));
                instance.setValue(attributes.get(1), data.getRiskScores().get(factor));
                dataset.add(instance);
            }

            // Appliquer le filtre StringToWordVector
            StringToWordVector filter = new StringToWordVector();
            filter.setInputFormat(dataset);
            filter.setIDFTransform(true);
            filter.setTFTransform(true);
            filter.setLowerCaseTokens(true);
            filter.setOutputWordCounts(true);

            Instances filteredData = Filter.useFilter(dataset, filter);

            // Entraîner le modèle SMO (Support Vector Machine)
            SMO regressor = new SMO();
            regressor.buildClassifier(filteredData);

            // Évaluer le modèle
            Evaluation evaluation = new Evaluation(filteredData);
            evaluation.crossValidateModel(regressor, filteredData, 10, new java.util.Random(1));

            logger.info("=== Évaluation du modèle pour " + factor + " ===");
            logger.info(evaluation.toSummaryString());

            // Sauvegarder le modèle
            modelManager.saveModel(factor + "_regressor", regressor);

            // Résultats
            Map<String, Object> factorResults = new HashMap<>();
            factorResults.put("correlation", evaluation.correlationCoefficient());
            factorResults.put("meanAbsoluteError", evaluation.meanAbsoluteError());
            factorResults.put("instances", dataset.numInstances());

            results.put(factor, factorResults);
        }

        return results;
    }

    /**
     * Classe interne pour représenter les données d'entraînement
     */
    private static class TrainingData {
        private String text;
        private String sentiment;
        private Map<String, Double> riskScores;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }

        public Map<String, Double> getRiskScores() {
            return riskScores;
        }

        public void setRiskScores(Map<String, Double> riskScores) {
            this.riskScores = riskScores;
        }
    }
}