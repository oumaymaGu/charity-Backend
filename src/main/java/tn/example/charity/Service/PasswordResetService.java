package tn.example.charity.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tn.example.charity.Entity.User;
import tn.example.charity.Repository.UserRepository;
import tn.example.charity.exception.BadRequestException;
import tn.example.charity.exception.NotFoundException;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordValidatorService passwordValidatorService;

    /**
     * Generate a reset token for the user and send an email
     */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new NotFoundException("User not found with email: " + email);
        }

        // Generate a unique token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        // Send email with reset link
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    /**
     * Reset the password using the token
     */
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);

        if (user == null) {
            throw new BadRequestException("Invalid or expired password reset token");
        }

        // Validate the new password
        if (!passwordValidatorService.validatePassword(newPassword)) {
            throw new BadRequestException("Password is not strong enough");
        }

        // Update the password and clear the reset token
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }
}