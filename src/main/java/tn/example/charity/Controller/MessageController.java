package tn.example.charity.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.dto.MessageDTO;
import tn.example.charity.Service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    private final MessageService messageService;

    /**
     * Pour récupérer l’historique d'une conversation entre deux utilisateurs et pour une association spécifique
     */
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam Long idAss) {
        List<MessageDTO> messages = messageService.getConversation(userId1, userId2, idAss);
        return ResponseEntity.ok(messages);
    }
}
