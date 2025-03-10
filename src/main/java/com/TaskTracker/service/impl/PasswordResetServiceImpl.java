package com.TaskTracker.service.impl;

import com.TaskTracker.entity.Account;
import com.TaskTracker.entity.PasswordResetToken;
import com.TaskTracker.repo.AccountRepo;
import com.TaskTracker.repo.PasswordResetTokenRepo;
import com.TaskTracker.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private final AccountRepo accountRepo;
    private final PasswordResetTokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Autowired
    public PasswordResetServiceImpl(AccountRepo accountRepo, PasswordResetTokenRepo tokenRepo,
                                    PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.accountRepo = accountRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public void requestPasswordReset(String email) {
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setAccount(account);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Ważny 1h

        tokenRepo.save(resetToken);

        sendResetEmail(account.getEmail(), token);
    }

    private void sendResetEmail(String email, String token) {
        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Click the link to reset your password: " + resetUrl);
        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expired");
        }

        Account account = resetToken.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepo.save(account);

        tokenRepo.delete(resetToken); // Usuwamy token po użyciu
    }
}
