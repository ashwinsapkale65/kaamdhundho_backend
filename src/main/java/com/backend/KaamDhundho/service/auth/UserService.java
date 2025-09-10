package com.backend.KaamDhundho.service.auth;

import com.backend.KaamDhundho.config.JwtService;
import com.backend.KaamDhundho.entity.auth.EmailVerificationToken;
import com.backend.KaamDhundho.entity.auth.User;
import com.backend.KaamDhundho.repository.auth.EmailVerificationTokenRepository;
import com.backend.KaamDhundho.repository.auth.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private JwtService jwtService;

    public Long extractUserIdFromToken(String token) {
        return jwtService.extractUserId(token); // Implement this in JwtService
    }


    @Transactional
    public void sendVerificationEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Invalidate old tokens
        emailVerificationTokenRepository.deleteByUserId(userId);

        // Generate new token
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(15, ChronoUnit.MINUTES); // 15 min validity

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(expiry);
        emailVerificationTokenRepository.save(verificationToken);

        // Build link
        String verificationLink = "http://localhost:8081/api/auth/verify-email?token=" + token;

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verify your email");
        message.setText("Click the link to verify your email: " + verificationLink);

        mailSender.send(message);
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.set_email_verified(true);
        userRepository.save(user);

        // Token should not be reused
        emailVerificationTokenRepository.deleteByUserId(user.getId());

        logger.info("User email verified successfully for userId={}", user.getId());

        messagingTemplate.convertAndSend("/topic/verification/" + user.getId(), "VERIFIED");
    }

}

