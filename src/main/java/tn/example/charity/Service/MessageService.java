package tn.example.charity.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Message;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.AssociationsRepository;
import tn.example.charity.Repository.MessageRepository;
import tn.example.charity.Repository.UserRepository;
import tn.example.charity.dto.MessageDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    @Autowired
    private final MessageRepository messageRepository;

    @Autowired
    private final AssociationsRepository associationRepository;

    @Autowired
    private final UserRepository userRepository;

    /**
     * Transforme un DTO en entité JPA avant persistance.
     */
    public Message fromDTO(MessageDTO dto) {
        Message entity = new Message();
        entity.setSenderId(dto.getSenderId());
        entity.setReceiverId(dto.getReceiverId());
        entity.setContent(dto.getContent());
        entity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());

        if (dto.getIdAss() != null) {
            associationRepository.findById(dto.getIdAss())
                    .ifPresent(entity::setAssociation);
        }
        return entity;
    }

    /**
     * Transforme une entité JPA en DTO, en y injectant aussi le nom d'utilisateur de l'expéditeur.
     */
    public MessageDTO toDTO(Message entity) {
        MessageDTO dto = new MessageDTO();
        dto.setSenderId(entity.getSenderId());
        dto.setReceiverId(entity.getReceiverId());
        dto.setContent(entity.getContent());
        dto.setTimestamp(entity.getTimestamp());

        if (entity.getAssociation() != null) {
            dto.setIdAss(entity.getAssociation().getIdAss());
        }

        // --- injection du username de l'expéditeur ---
        Optional<User> userOpt = userRepository.findById(entity.getSenderId());
        dto.setSenderUsername(userOpt.map(User::getUsername).orElse("Inconnu"));

        return dto;
    }

    /**
     * Sauvegarde un message en base et renvoie le DTO enrichi.
     */
    public MessageDTO saveMessage(MessageDTO dto) {
        Message saved = messageRepository.save(fromDTO(dto));
        return toDTO(saved);
    }

    /**
     * Récupère l'historique d'une conversation entre deux utilisateurs, éventuellement filtré par association.
     */
    public List<MessageDTO> getConversation(Long user1, Long user2, Long idAss) {
        List<Message> messages;
        if (idAss != null) {
            messages = messageRepository
                    .findBySenderIdAndReceiverIdAndAssociationIdAssOrReceiverIdAndSenderIdAndAssociationIdAssOrderByTimestampAsc(
                            user1, user2, idAss,
                            user1, user2, idAss
                    );
        } else {
            messages = messageRepository
                    .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                            user1, user2,
                            user2, user1
                    );
        }
        return messages.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
