package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Service.MessageService;
import tn.example.charity.dto.MessageDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// src/main/java/tn/example/charity/Controller/ChatWebSocketController.java
@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class ChatWebSocketController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        // 1) Sauvegarde & récupération du DTO enrichi (avec senderUsername)
        MessageDTO savedMessage = messageService.saveMessage(messageDTO);

        // 2) Envoi au destinataire
        messagingTemplate.convertAndSend(
                "/topic/messages/" + savedMessage.getReceiverId()
                        + "/association/" + savedMessage.getIdAss(),
                savedMessage
        );

        // 3) Envoi à l’admin
        messagingTemplate.convertAndSend("/topic/admin", savedMessage);

        System.out.println("Message reçu et broadcasté : " + savedMessage);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());
        return ResponseEntity.ok("/uploads/" + fileName);
    }

}
