package tn.example.charity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime timestamp;
    private Long idAss;
    private String senderUsername;


}

