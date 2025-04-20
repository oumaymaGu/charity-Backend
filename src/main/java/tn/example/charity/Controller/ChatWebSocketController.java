package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import tn.example.charity.Service.MessageService;
import tn.example.charity.dto.MessageDTO;

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
}
