package tn.example.charity.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.example.charity.Entity.*;
import tn.example.charity.Repository.QuestionRepository;
import tn.example.charity.Repository.SupportServiceRepository;
import java.util.Arrays;

@Configuration
public class DataInitializer {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SupportServiceRepository serviceRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Initialiser uniquement si aucune question n'existe déjà
            if (questionRepository.count() == 0) {
                initializeQuestions();
            }

            // Initialiser uniquement si aucun service n'existe déjà
            if (serviceRepository.count() == 0) {
                initializeSupportServices();
            }
        };
    }

    private void initializeQuestions() {
        // Questions sur les pensées violentes
        Question q1 = new Question();
        q1.setQuestionText("Avez-vous eu des pensées de faire du mal à d'autres personnes au cours des deux dernières semaines?");
        q1.setType(QuestionType.YES_NO);
        q1.setChecksViolentThoughts(true);
        q1.setChecksParanoia(false);
        q1.setChecksImpulsivity(false);
        q1.setChecksSocialIsolation(false);
        q1.setChecksSubstanceAbuse(false);
        q1.setChecksHallucinations(false);
        q1.setChecksHostility(true);
        q1.setWeight(1.0);
        q1.setCategory(QuestionCategory.THOUGHTS);
        questionRepository.save(q1);

        Question q2 = new Question();
        q2.setQuestionText("Sur une échelle de 0 à 10, à quelle fréquence avez-vous des pensées violentes?");
        q2.setType(QuestionType.SCALE);
        q2.setChecksViolentThoughts(true);
        q2.setChecksParanoia(false);
        q2.setChecksImpulsivity(false);
        q2.setChecksSocialIsolation(false);
        q2.setChecksSubstanceAbuse(false);
        q2.setChecksHallucinations(false);
        q2.setChecksHostility(true);
        q2.setWeight(0.8);
        q2.setCategory(QuestionCategory.THOUGHTS);
        questionRepository.save(q2);

        // Questions sur la paranoïa
        Question q3 = new Question();
        q3.setQuestionText("Pensez-vous que des gens vous suivent ou vous surveillent?");
        q3.setType(QuestionType.YES_NO);
        q3.setChecksViolentThoughts(false);
        q3.setChecksParanoia(true);
        q3.setChecksImpulsivity(false);
        q3.setChecksSocialIsolation(false);
        q3.setChecksSubstanceAbuse(false);
        q3.setChecksHallucinations(true);
        q3.setChecksHostility(false);
        q3.setWeight(0.9);
        q3.setCategory(QuestionCategory.THOUGHTS);
        questionRepository.save(q3);

        Question q4 = new Question();
        q4.setQuestionText("Comment décririez-vous votre méfiance envers les autres?");
        q4.setType(QuestionType.MULTIPLE_CHOICE);
        q4.setPossibleAnswers(Arrays.asList(
                "Je fais généralement confiance aux autres",
                "Je suis parfois méfiant",
                "Je suis souvent méfiant",
                "Je ne fais confiance à personne"
        ));
        q4.setChecksViolentThoughts(false);
        q4.setChecksParanoia(true);
        q4.setChecksImpulsivity(false);
        q4.setChecksSocialIsolation(true);
        q4.setChecksSubstanceAbuse(false);
        q4.setChecksHallucinations(false);
        q4.setChecksHostility(false);
        q4.setWeight(0.7);
        q4.setCategory(QuestionCategory.SOCIAL);
        questionRepository.save(q4);

        // Questions sur les hallucinations
        Question q5 = new Question();
        q5.setQuestionText("Entendez-vous des voix ou voyez-vous des choses que d'autres ne semblent pas entendre ou voir?");
        q5.setType(QuestionType.YES_NO);
        q5.setChecksViolentThoughts(false);
        q5.setChecksParanoia(false);
        q5.setChecksImpulsivity(false);
        q5.setChecksSocialIsolation(false);
        q5.setChecksSubstanceAbuse(false);
        q5.setChecksHallucinations(true);
        q5.setChecksHostility(false);
        q5.setWeight(1.0);
        q5.setCategory(QuestionCategory.THOUGHTS);
        questionRepository.save(q5);

        // Questions sur l'impulsivité
        Question q6 = new Question();
        q6.setQuestionText("Avez-vous des difficultés à contrôler vos impulsions?");
        q6.setType(QuestionType.YES_NO);
        q6.setChecksViolentThoughts(false);
        q6.setChecksParanoia(false);
        q6.setChecksImpulsivity(true);
        q6.setChecksSocialIsolation(false);
        q6.setChecksSubstanceAbuse(false);
        q6.setChecksHallucinations(false);
        q6.setChecksHostility(true);
        q6.setWeight(0.9);
        q6.setCategory(QuestionCategory.BEHAVIOR);
        questionRepository.save(q6);

        Question q7 = new Question();
        q7.setQuestionText("Comment évalueriez-vous votre capacité à vous calmer lorsque vous êtes en colère?");
        q7.setType(QuestionType.MULTIPLE_CHOICE);
        q7.setPossibleAnswers(Arrays.asList(
                "Je peux généralement me calmer facilement",
                "J'ai parfois du mal à me calmer",
                "J'ai souvent du mal à me calmer",
                "Je n'arrive presque jamais à me calmer quand je suis en colère"
        ));
        q7.setChecksViolentThoughts(false);
        q7.setChecksParanoia(false);
        q7.setChecksImpulsivity(true);
        q7.setChecksSocialIsolation(false);
        q7.setChecksSubstanceAbuse(false);
        q7.setChecksHallucinations(false);
        q7.setChecksHostility(true);
        q7.setWeight(0.8);
        q7.setCategory(QuestionCategory.EMOTIONS);
        questionRepository.save(q7);

        // Questions sur l'isolement social
        Question q8 = new Question();
        q8.setQuestionText("À quelle fréquence vous sentez-vous isolé ou seul?");
        q8.setType(QuestionType.SCALE);
        q8.setChecksViolentThoughts(false);
        q8.setChecksParanoia(false);
        q8.setChecksImpulsivity(false);
        q8.setChecksSocialIsolation(true);
        q8.setChecksSubstanceAbuse(false);
        q8.setChecksHallucinations(false);
        q8.setChecksHostility(false);
        q8.setWeight(0.7);
        q8.setCategory(QuestionCategory.SOCIAL);
        questionRepository.save(q8);

        // Questions sur l'abus de substances
        Question q9 = new Question();
        q9.setQuestionText("Consommez-vous régulièrement de l'alcool ou des drogues?");
        q9.setType(QuestionType.MULTIPLE_CHOICE);
        q9.setPossibleAnswers(Arrays.asList(
                "Jamais",
                "Occasionnellement",
                "Régulièrement",
                "Tous les jours"
        ));
        q9.setChecksViolentThoughts(false);
        q9.setChecksParanoia(false);
        q9.setChecksImpulsivity(true);
        q9.setChecksSocialIsolation(false);
        q9.setChecksSubstanceAbuse(true);
        q9.setChecksHallucinations(false);
        q9.setChecksHostility(false);
        q9.setWeight(0.8);
        q9.setCategory(QuestionCategory.SUBSTANCES);
        questionRepository.save(q9);

        // Questions sur les antécédents
        Question q10 = new Question();
        q10.setQuestionText("Avez-vous déjà eu des problèmes avec la loi en raison de comportements violents?");
        q10.setType(QuestionType.YES_NO);
        q10.setChecksViolentThoughts(true);
        q10.setChecksParanoia(false);
        q10.setChecksImpulsivity(false);
        q10.setChecksSocialIsolation(false);
        q10.setChecksSubstanceAbuse(false);
        q10.setChecksHallucinations(false);
        q10.setChecksHostility(true);
        q10.setWeight(1.0);
        q10.setCategory(QuestionCategory.HISTORY);
        questionRepository.save(q10);
    }

    private void initializeSupportServices() {
        // Services d'urgence psychiatrique
        SupportService s1 = new SupportService();
        s1.setName("Centre d'Urgence Psychiatrique");
        s1.setDescription("Service d'intervention immédiate pour les situations de crise psychiatrique");
        s1.setType(ServiceType.PSYCHIATRIC_EMERGENCY);
        s1.setEmergencyService(true);
        s1.setContactPhone("15 ou 112");
        s1.setContactEmail("urgence@hopital.fr");
        s1.setAddress("Hôpital Universitaire le plus proche");
        s1.setAvailable24h(true);
        s1.setSpecializesInViolence(true);
        s1.setSpecializesInParanoia(true);
        s1.setSpecializesInImpulsivity(true);
        s1.setSpecializesInSocialSupport(false);
        s1.setSpecializesInSubstanceAbuse(false);
        s1.setSpecializesInAngerManagement(true);
        serviceRepository.save(s1);

        // Service de crise
        SupportService s2 = new SupportService();
        s2.setName("SOS Amitié");
        s2.setDescription("Service d'écoute téléphonique pour les personnes en détresse psychologique");
        s2.setType(ServiceType.CRISIS_INTERVENTION);
        s2.setEmergencyService(true);
        s2.setContactPhone("09 72 39 40 50");
        s2.setContactEmail("contact@sos-amitie.com");
        s2.setAddress("N/A - Service téléphonique");
        s2.setAvailable24h(true);
        s2.setSpecializesInViolence(false);
        s2.setSpecializesInParanoia(false);
        s2.setSpecializesInImpulsivity(false);
        s2.setSpecializesInSocialSupport(true);
        s2.setSpecializesInSubstanceAbuse(false);
        s2.setSpecializesInAngerManagement(false);
        serviceRepository.save(s2);

        // Service de consultation psychiatrique
        SupportService s3 = new SupportService();
        s3.setName("Centre Médico-Psychologique (CMP)");
        s3.setDescription("Service public de consultation psychiatrique");
        s3.setType(ServiceType.OUTPATIENT_TREATMENT);
        s3.setEmergencyService(false);
        s3.setContactPhone("Contactez le CMP local");
        s3.setContactEmail("contact@cmp.fr");
        s3.setAddress("Adresse du CMP le plus proche");
        s3.setAvailable24h(false);
        s3.setSpecializesInViolence(false);
        s3.setSpecializesInParanoia(true);
        s3.setSpecializesInImpulsivity(false);
        s3.setSpecializesInSocialSupport(true);
        s3.setSpecializesInSubstanceAbuse(false);
        s3.setSpecializesInAngerManagement(false);
        serviceRepository.save(s3);

        // Service pour abus de substances
        SupportService s4 = new SupportService();
        s4.setName("Centre de Soins, d'Accompagnement et de Prévention en Addictologie (CSAPA)");
        s4.setDescription("Centre spécialisé dans le traitement des addictions");
        s4.setType(ServiceType.SUBSTANCE_ABUSE_TREATMENT);
        s4.setEmergencyService(false);
        s4.setContactPhone("Contactez le CSAPA local");
        s4.setContactEmail("contact@csapa.fr");
        s4.setAddress("Adresse du CSAPA le plus proche");
        s4.setAvailable24h(false);
        s4.setSpecializesInViolence(false);
        s4.setSpecializesInParanoia(false);
        s4.setSpecializesInImpulsivity(true);
        s4.setSpecializesInSocialSupport(false);
        s4.setSpecializesInSubstanceAbuse(true);
        s4.setSpecializesInAngerManagement(false);
        serviceRepository.save(s4);

        // Groupe de soutien
        SupportService s5 = new SupportService();
        s5.setName("Groupes d'Entraide Mutuelle (GEM)");
        s5.setDescription("Espaces d'accueil et d'entraide animés par et pour des personnes vivant avec des troubles psychiques");
        s5.setType(ServiceType.SUPPORT_GROUP);
        s5.setEmergencyService(false);
        s5.setContactPhone("Varie selon les structures");
        s5.setContactEmail("contact@gem-france.fr");
        s5.setAddress("Adresses variables selon les structures");
        s5.setAvailable24h(false);
        s5.setSpecializesInViolence(false);
        s5.setSpecializesInParanoia(false);
        s5.setSpecializesInImpulsivity(false);
        s5.setSpecializesInSocialSupport(true);
        s5.setSpecializesInSubstanceAbuse(false);
        s5.setSpecializesInAngerManagement(false);
        serviceRepository.save(s5);
    }
}