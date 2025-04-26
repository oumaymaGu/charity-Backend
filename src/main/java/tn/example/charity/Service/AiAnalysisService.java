package tn.example.charity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.PsychologicalEvaluation;
import tn.example.charity.Entity.RiskLevel;
import tn.example.charity.Entity.SupportService;
import tn.example.charity.ai.ModelManager;
import tn.example.charity.ai.TextPreprocessor;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service d'analyse IA pour l'évaluation psychologique
 */
@Service
public class AiAnalysisService {

    private static final Logger logger = Logger.getLogger(AiAnalysisService.class.getName());

    @Autowired
    private ModelManager modelManager;

    @Autowired
    private TextPreprocessor textPreprocessor;

    // Mots-clés pour l'analyse de texte
    private final Map<String, Double> traumaKeywords = new HashMap<>();
    private final Map<String, Double> violenceKeywords = new HashMap<>();
    private final Map<String, Double> paranoiaKeywords = new HashMap<>();
    private final Map<String, Double> depressionKeywords = new HashMap<>();
    private final Map<String, Double> anxietyKeywords = new HashMap<>();

    public AiAnalysisService() {
        initializeKeywords();
    }

    private void initializeKeywords() {
        // Mots-clés liés aux traumatismes (avec poids)
        traumaKeywords.put("cauchemar", 0.7);
        traumaKeywords.put("flashback", 0.8);
        traumaKeywords.put("peur", 0.5);
        traumaKeywords.put("terreur", 0.7);
        traumaKeywords.put("choc", 0.6);
        traumaKeywords.put("traumatisme", 0.9);
        traumaKeywords.put("abus", 0.8);
        traumaKeywords.put("guerre", 0.7);
        traumaKeywords.put("mort", 0.6);
        traumaKeywords.put("violence", 0.7);

        // Mots-clés liés à la violence
        violenceKeywords.put("frapper", 0.8);
        violenceKeywords.put("tuer", 0.9);
        violenceKeywords.put("blesser", 0.7);
        violenceKeywords.put("colère", 0.6);
        violenceKeywords.put("rage", 0.8);
        violenceKeywords.put("détruire", 0.7);
        violenceKeywords.put("arme", 0.8);
        violenceKeywords.put("menace", 0.7);
        violenceKeywords.put("agression", 0.8);

        // Mots-clés liés à la paranoïa
        paranoiaKeywords.put("surveillance", 0.7);
        paranoiaKeywords.put("complot", 0.8);
        paranoiaKeywords.put("méfiance", 0.6);
        paranoiaKeywords.put("espionner", 0.7);
        paranoiaKeywords.put("persécution", 0.8);
        paranoiaKeywords.put("suivre", 0.5);
        paranoiaKeywords.put("danger", 0.6);
        paranoiaKeywords.put("menace", 0.7);

        // Mots-clés liés à la dépression
        depressionKeywords.put("triste", 0.6);
        depressionKeywords.put("déprimé", 0.8);
        depressionKeywords.put("désespoir", 0.8);
        depressionKeywords.put("inutile", 0.7);
        depressionKeywords.put("vide", 0.6);
        depressionKeywords.put("fatigue", 0.5);
        depressionKeywords.put("sommeil", 0.4);
        depressionKeywords.put("appétit", 0.4);
        depressionKeywords.put("suicide", 0.9);
        depressionKeywords.put("mort", 0.7);

        // Mots-clés liés à l'anxiété
        anxietyKeywords.put("anxieux", 0.7);
        anxietyKeywords.put("panique", 0.8);
        anxietyKeywords.put("inquiet", 0.6);
        anxietyKeywords.put("stress", 0.6);
        anxietyKeywords.put("tension", 0.5);
        anxietyKeywords.put("nerveux", 0.6);
        anxietyKeywords.put("peur", 0.7);
        anxietyKeywords.put("angoisse", 0.8);
        anxietyKeywords.put("respiration", 0.5);
        anxietyKeywords.put("cœur", 0.4);
    }

    /**
     * Analyse un texte pour détecter des signes de détresse psychologique
     * @param text Texte à analyser
     * @return Résultats de l'analyse
     */
    public Map<String, Object> analyzeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return createEmptyAnalysis();
        }

        // Vérifier si les modèles sont chargés
        if (modelManager.areModelsLoaded() && modelManager.getModel("sentiment_classifier") != null) {
            try {
                logger.info("Analyse du texte avec le modèle d'IA");
                return analyzeTextWithModel(text);
            } catch (Exception e) {
                logger.warning("Erreur lors de l'analyse avec le modèle: " + e.getMessage());
                // En cas d'erreur, utiliser l'approche basée sur des règles
                logger.info("Repli sur l'analyse basée sur des règles");
                return analyzeTextWithRules(text);
            }
        } else {
            // Utiliser l'approche basée sur des règles
            logger.info("Modèles non disponibles, utilisation de l'analyse basée sur des règles");
            return analyzeTextWithRules(text);
        }
    }

    /**
     * Analyse le texte en utilisant le modèle d'IA
     */
    private Map<String, Object> analyzeTextWithModel(String text) throws Exception {
        Map<String, Object> result = new HashMap<>();

        // Prétraiter le texte
        String processedText = textPreprocessor.preprocess(text);

        // Prédire le sentiment
        String sentiment = predictSentiment(processedText);
        result.put("sentiment", sentiment);

        // Prédire les scores de risque
        Map<String, Double> riskScores = predictRiskScores(processedText);
        result.put("scores", riskScores);

        // Extraire les mots-clés (utiliser l'approche basée sur des règles pour cela)
        List<String> keywords = extractKeywords(text);
        result.put("keywords", keywords);

        // Déterminer le niveau de détresse
        double maxScore = riskScores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        String distressLevel;
        if (maxScore > 0.7) distressLevel = "sévère";
        else if (maxScore > 0.5) distressLevel = "élevé";
        else if (maxScore > 0.3) distressLevel = "modéré";
        else distressLevel = "faible";

        result.put("distressLevel", distressLevel);

        // Déterminer s'il y a des indicateurs de trauma
        boolean traumaIndicators = maxScore > 0.6 ||
                "très négatif".equals(sentiment) ||
                riskScores.getOrDefault("violentThoughts", 0.0) > 0.7;

        result.put("traumaIndicators", traumaIndicators);

        return result;
    }

    /**
     * Prédit le sentiment  traumaIndicators);

     return result;
     }

     /**
     * Prédit le sentiment en utilisant le modèle de classification
     */
    private String predictSentiment(String text) throws Exception {
        Classifier sentimentModel = modelManager.getModel("sentiment_classifier");

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
        Instances dataset = new Instances("TextAnalysis", attributes, 1);
        dataset.setClassIndex(1);

        // Ajouter l'instance
        DenseInstance instance = new DenseInstance(2);
        instance.setValue(attributes.get(0), text);
        dataset.add(instance);

        // Configurer le filtre StringToWordVector
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(dataset);
        filter.setIDFTransform(true);
        filter.setTFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);

        // Appliquer le filtre
        Instances filteredData = Filter.useFilter(dataset, filter);

        // Prédire le sentiment
        double prediction = sentimentModel.classifyInstance(filteredData.instance(0));
        return dataset.classAttribute().value((int) prediction);
    }

    /**
     * Prédit les scores de risque en utilisant les modèles de régression
     */
    private Map<String, Double> predictRiskScores(String text) throws Exception {
        Map<String, Double> riskScores = new HashMap<>();
        String[] riskFactors = {"violentThoughts", "paranoia", "impulsivity", "socialIsolation", "substanceAbuse", "hostility"};

        for (String factor : riskFactors) {
            Classifier riskModel = modelManager.getModel(factor + "_regressor");

            if (riskModel != null) {
                // Créer les attributs pour le dataset Weka
                ArrayList<Attribute> attributes = new ArrayList<>();

                // Attribut pour le texte
                attributes.add(new Attribute("text", (ArrayList<String>) null));

                // Attribut pour le score
                attributes.add(new Attribute("score"));

                // Créer le dataset
                Instances dataset = new Instances(factor + "Analysis", attributes, 1);
                dataset.setClassIndex(1);

                // Ajouter l'instance
                DenseInstance instance = new DenseInstance(2);
                instance.setValue(attributes.get(0), text);
                dataset.add(instance);

                // Configurer le filtre StringToWordVector
                StringToWordVector filter = new StringToWordVector();
                filter.setInputFormat(dataset);
                filter.setIDFTransform(true);
                filter.setTFTransform(true);
                filter.setLowerCaseTokens(true);
                filter.setOutputWordCounts(true);

                // Appliquer le filtre
                Instances filteredData = Filter.useFilter(dataset, filter);

                // Prédire le score
                double score = riskModel.classifyInstance(filteredData.instance(0));

                // Limiter le score entre 0 et 1
                score = Math.max(0, Math.min(1, score));

                riskScores.put(factor, score);
            } else {
                // Utiliser l'approche basée sur des règles pour ce facteur
                riskScores.put(factor, calculateRiskScoreWithRules(text, factor));
            }
        }

        return riskScores;
    }

    /**
     * Extrait les mots-clés pertinents du texte
     */
    private List<String> extractKeywords(String text) {
        // Utiliser l'approche basée sur des règles pour extraire les mots-clés
        List<String> keywords = new ArrayList<>();
        String normalizedText = text.toLowerCase();

        // Vérifier chaque mot-clé dans le texte
        checkKeywords(normalizedText, traumaKeywords, keywords);
        checkKeywords(normalizedText, violenceKeywords, keywords);
        checkKeywords(normalizedText, paranoiaKeywords, keywords);
        checkKeywords(normalizedText, depressionKeywords, keywords);
        checkKeywords(normalizedText, anxietyKeywords, keywords);

        return keywords;
    }

    private void checkKeywords(String text, Map<String, Double> keywordMap, List<String> detectedKeywords) {
        for (String keyword : keywordMap.keySet()) {
            if (text.contains(keyword) && !detectedKeywords.contains(keyword)) {
                detectedKeywords.add(keyword);
            }
        }
    }

    /**
     * Analyse le texte en utilisant l'approche basée sur des règles
     */
    private Map<String, Object> analyzeTextWithRules(String text) {
        String normalizedText = text.toLowerCase();

        // Détection des mots-clés
        List<String> detectedKeywords = new ArrayList<>();
        double traumaScore = calculateKeywordScore(normalizedText, traumaKeywords, detectedKeywords);
        double violenceScore = calculateKeywordScore(normalizedText, violenceKeywords, detectedKeywords);
        double paranoiaScore = calculateKeywordScore(normalizedText, paranoiaKeywords, detectedKeywords);
        double depressionScore = calculateKeywordScore(normalizedText, depressionKeywords, detectedKeywords);
        double anxietyScore = calculateKeywordScore(normalizedText, anxietyKeywords, detectedKeywords);

        // Analyse du sentiment
        String sentiment = determineSentiment(traumaScore, violenceScore, depressionScore, anxietyScore);

        // Niveau de détresse
        String distressLevel = determineDistressLevel(traumaScore, violenceScore, depressionScore, anxietyScore);

        // Indicateurs de trauma
        boolean traumaIndicators = traumaScore > 0.4 ||
                (depressionScore > 0.6 && anxietyScore > 0.6) ||
                violenceScore > 0.7;

        // Résultat de l'analyse
        Map<String, Object> result = new HashMap<>();
        result.put("sentiment", sentiment);
        result.put("keywords", detectedKeywords);
        result.put("distressLevel", distressLevel);
        result.put("traumaIndicators", traumaIndicators);
        result.put("scores", Map.of(
                "trauma", traumaScore,
                "violence", violenceScore,
                "paranoia", paranoiaScore,
                "depression", depressionScore,
                "anxiety", anxietyScore,
                "violentThoughts", violenceScore,
                "impulsivity", violenceScore * 0.7,
                "socialIsolation", depressionScore * 0.8,
                "substanceAbuse", 0.2,
                "hostility", violenceScore * 0.9
        ));

        return result;
    }

    /**
     * Calcule un score de risque pour un facteur spécifique en utilisant l'approche basée sur des règles
     */
    private double calculateRiskScoreWithRules(String text, String factor) {
        String normalizedText = text.toLowerCase();
        List<String> detectedKeywords = new ArrayList<>();

        switch (factor) {
            case "violentThoughts":
                return calculateKeywordScore(normalizedText, violenceKeywords, detectedKeywords);
            case "paranoia":
                return calculateKeywordScore(normalizedText, paranoiaKeywords, detectedKeywords);
            case "impulsivity":
                // Simplification: utiliser un score basé sur la violence
                return calculateKeywordScore(normalizedText, violenceKeywords, detectedKeywords) * 0.7;
            case "socialIsolation":
                // Simplification: utiliser un score basé sur la dépression
                return calculateKeywordScore(normalizedText, depressionKeywords, detectedKeywords) * 0.8;
            case "substanceAbuse":
                // Simplification: score fixe bas
                return 0.2;
            case "hostility":
                // Simplification: utiliser un score basé sur la violence
                return calculateKeywordScore(normalizedText, violenceKeywords, detectedKeywords) * 0.9;
            default:
                return 0.0;
        }
    }

    /**
     * Calcule un score basé sur la présence de mots-clés dans le texte
     */
    private double calculateKeywordScore(String text, Map<String, Double> keywords, List<String> detectedKeywords) {
        double score = 0;
        int matches = 0;

        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
            String keyword = entry.getKey();
            double weight = entry.getValue();

            // Recherche du mot-clé dans le texte
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                score += weight;
                matches++;
                if (!detectedKeywords.contains(keyword)) {
                    detectedKeywords.add(keyword);
                }
            }
        }

        // Normaliser le score
        return matches > 0 ? Math.min(1.0, score / matches) : 0;
    }

    /**
     * Détermine le sentiment basé sur les scores
     */
    private String determineSentiment(double traumaScore, double violenceScore, double depressionScore, double anxietyScore) {
        double negativeScore = Math.max(traumaScore, Math.max(violenceScore, Math.max(depressionScore, anxietyScore)));

        if (negativeScore > 0.7) return "très négatif";
        if (negativeScore > 0.5) return "négatif";
        if (negativeScore > 0.3) return "légèrement négatif";
        return "neutre";
    }

    /**
     * Détermine le niveau de détresse basé sur les scores
     */
    private String determineDistressLevel(double traumaScore, double violenceScore, double depressionScore, double anxietyScore) {
        double maxScore = Math.max(traumaScore, Math.max(violenceScore, Math.max(depressionScore, anxietyScore)));

        if (maxScore > 0.7) return "sévère";
        if (maxScore > 0.5) return "élevé";
        if (maxScore > 0.3) return "modéré";
        return "faible";
    }

    /**
     * Crée une analyse vide pour les cas où le texte est vide
     */
    private Map<String, Object> createEmptyAnalysis() {
        Map<String, Object> result = new HashMap<>();
        result.put("sentiment", "neutre");
        result.put("keywords", new ArrayList<String>());
        result.put("distressLevel", "faible");
        result.put("traumaIndicators", false);
        result.put("scores", Map.of(
                "trauma", 0.0,
                "violence", 0.0,
                "paranoia", 0.0,
                "depression", 0.0,
                "anxiety", 0.0,
                "violentThoughts", 0.0,
                "impulsivity", 0.0,
                "socialIsolation", 0.0,
                "substanceAbuse", 0.0,
                "hostility", 0.0
        ));

        return result;
    }

    /**
     * Génère des recommandations basées sur l'évaluation psychologique
     */


    /**
     * Crée des services de support d'exemple
     */
    private List<SupportService> createSampleSupportServices() {
        List<SupportService> services = new ArrayList<>();

        // Service d'urgence psychologique
        SupportService emergencyService = new SupportService();
        emergencyService.setId(1L);
        emergencyService.setName("Service d'urgence psychologique");
        emergencyService.setDescription("Intervention immédiate pour les cas de détresse psychologique sévère");
        emergencyService.setContactPhone("Tél: 15 ou 112");
        emergencyService.setEmergencyService(true);
        emergencyService.setSpecializesInViolence(true);
        emergencyService.setSpecializesInParanoia(true);


        services.add(emergencyService);

        // Centre de thérapie spécialisé en trauma
        SupportService traumaCenter = new SupportService();
        traumaCenter.setId(2L);
        traumaCenter.setName("Centre de thérapie spécialisé en trauma");
        traumaCenter.setDescription("Thérapie spécialisée pour les victimes de traumatismes");
        traumaCenter.setContactPhone("contact@trauma-center.org - Tél: 01 23 45 67 89");
        traumaCenter.setEmergencyService(false);
        traumaCenter.setSpecializesInViolence(false);
        traumaCenter.setSpecializesInParanoia(false);
        services.add(traumaCenter);

        // Groupe de soutien en ligne
        SupportService onlineSupport = new SupportService();
        onlineSupport.setId(3L);
        onlineSupport.setName("Groupe de soutien en ligne");
        onlineSupport.setDescription("Groupe de parole et d'entraide pour réfugiés");
        onlineSupport.setContactEmail("support@refugee-help.org");
        onlineSupport.setEmergencyService(false);
        onlineSupport.setSpecializesInViolence(false);
        onlineSupport.setSpecializesInParanoia(false);

        services.add(onlineSupport);

        // Centre de traitement des addictions
        SupportService addictionCenter = new SupportService();
        addictionCenter.setId(4L);
        addictionCenter.setName("Centre de traitement des addictions");
        addictionCenter.setDescription("Traitement spécialisé pour les problèmes de dépendance");
        addictionCenter.setContactEmail("addictions@medical-center.org - Tél: 01 98 76 54 32");
        addictionCenter.setEmergencyService(false);
        addictionCenter.setSpecializesInViolence(false);
        addictionCenter.setSpecializesInParanoia(false);
        addictionCenter.setSpecializesInSubstanceAbuse(true);

        services.add(addictionCenter);

        // Service de psychiatrie
        SupportService psychiatryService = new SupportService();
        psychiatryService.setId(5L);
        psychiatryService.setName("Service de psychiatrie");
        psychiatryService.setDescription("Consultation psychiatrique et suivi médical");
        psychiatryService.setContactEmail("psychiatrie@hopital.fr - Tél: 01 45 67 89 10");
        psychiatryService.setEmergencyService(false);
        psychiatryService.setSpecializesInViolence(true);
        psychiatryService.setSpecializesInParanoia(true);

        services.add(psychiatryService);

        return services;
    }
}