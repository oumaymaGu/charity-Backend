package tn.example.charity.Controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.example.charity.Service.PasswordResetService;
import tn.example.charity.dto.MessageResponse;
import tn.example.charity.dto.NewPasswordRequest;
import tn.example.charity.dto.ResetPassword;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ResetPassword resetRequest) {
        passwordResetService.requestPasswordReset(resetRequest.getEmail());
        return ResponseEntity.ok(new MessageResponse("Password reset email sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody NewPasswordRequest resetRequest) {
        passwordResetService.resetPassword(resetRequest.getToken(), resetRequest.getPassword());
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
    }
}