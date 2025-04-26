package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Entity.Logement;
import tn.example.charity.Entity.Refuge;
import tn.example.charity.Repository.LogementRepository;
import tn.example.charity.Repository.RefugeRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefugeServiceImpl implements IRefugeService {

    private final RefugeRepository refugeRepository;
    private final LogementRepository logementRepository;
    private final GenderDetectionService genderDetectionService;

    private final Path rootLocation = Paths.get("uploads/images/refuges");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public Refuge addRefuge(Refuge refuge) {
        return refugeRepository.save(refuge);
    }

    @Override
    public List<Refuge> getAllRefuge() {
        return refugeRepository.findAll();
    }

    @Override
    public Refuge getRefugeById(Long idRfg) {
        return refugeRepository.findById(idRfg)
                .orElseThrow(() -> new RuntimeException("Refuge not found with id: " + idRfg));
    }

    @Override
    public void deleteRefugeById(Long idRfg) {
        Refuge refuge = getRefugeById(idRfg);

        if (refuge.getImagePath() != null && !refuge.getImagePath().isEmpty()) {
            try {
                Path imagePath = rootLocation.resolve(refuge.getImagePath());
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.err.println("Failed to delete image file: " + e.getMessage());
            }
        }

        refugeRepository.deleteById(idRfg);
    }

    @Override
    public Refuge updateRefugeById(Refuge refuge) {
        Refuge existingRefuge = getRefugeById(refuge.getIdRfg());

        if (refuge.getImagePath() == null || refuge.getImagePath().isEmpty()) {
            refuge.setImagePath(existingRefuge.getImagePath());
        }

        return refugeRepository.save(refuge);
    }

    @Override
    public Refuge getLastRefuge() {
        return refugeRepository.findTopByOrderByIdRfgDesc();
    }

    @Override
    public Refuge uploadRefugeImage(Long idRfg, MultipartFile imageFile) throws IOException {
        Refuge refuge = getRefugeById(idRfg);

        // Delete previous image if exists
        if (refuge.getImagePath() != null && !refuge.getImagePath().isEmpty()) {
            Path oldImagePath = rootLocation.resolve(Paths.get(refuge.getImagePath()).getFileName());
            Files.deleteIfExists(oldImagePath);
        }

        // Generate unique filename and resolve destination path
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID() + fileExtension;

        Path destinationFile = rootLocation.resolve(newFilename).normalize();

        // Copy the uploaded file to the target location (replacing existing file with the same name)
        Files.copy(imageFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        // Save the relative or absolute path as needed
        // Recommended: save relative URL path accessible by clients
        String relativeImagePath = "/uploads/images/refuges/" + newFilename;
        refuge.setImagePath(relativeImagePath);

        // Call Face++ API to detect attributes
        GenderDetectionService.GenderDetectionResult detectionResult = genderDetectionService.detectGender(imageFile);

        // Update Refuge entity with detected attributes
        refuge.setDetectedGender(detectionResult.getGender());
        refuge.setGenderConfidence(detectionResult.getConfidence());
        refuge.setDetectedAge(detectionResult.getAge());

        refuge.setEmotionHappiness(detectionResult.getEmotionHappiness());
        refuge.setEmotionSadness(detectionResult.getEmotionSadness());
        refuge.setEmotionAnger(detectionResult.getEmotionAnger());
        refuge.setEmotionSurprise(detectionResult.getEmotionSurprise());
        refuge.setEmotionFear(detectionResult.getEmotionFear());
        refuge.setEmotionDisgust(detectionResult.getEmotionDisgust());
        refuge.setEmotionNeutral(detectionResult.getEmotionNeutral());

        return refugeRepository.save(refuge);
    }



    @Override
    public Refuge affectRefugeToLogement(Long idRefuge, Long idLogement) {
        Refuge refuge = getRefugeById(idRefuge);
        Logement logement = logementRepository.findById(idLogement)
                .orElseThrow(() -> new RuntimeException("Logement not found with id: " + idLogement));

        if (logement.getCapacite() <= 0) {
            throw new RuntimeException("Logement has no available capacity");
        }

        refuge.setLogement(logement);
        logement.setRefuge(refuge);
        logement.setCapacite(logement.getCapacite() - 1);

        if (logement.getCapacite() == 0) {
            logement.setDisponnibilite("Complet");
        } else {
            logement.setDisponnibilite("Partiellement occupé");
        }

        logementRepository.save(logement);
        return refugeRepository.save(refuge);
    }

    public ResponseEntity<Resource> serveImage(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
