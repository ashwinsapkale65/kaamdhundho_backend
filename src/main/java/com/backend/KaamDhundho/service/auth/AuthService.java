package com.backend.KaamDhundho.service.auth;

import com.backend.KaamDhundho.dto.AuthDto.AuthResponse;
import com.backend.KaamDhundho.dto.AuthDto.LoginRequest;
import com.backend.KaamDhundho.dto.AuthDto.RegisterRequest;
import com.backend.KaamDhundho.entity.auth.Session;
import com.backend.KaamDhundho.entity.auth.User;
import com.backend.KaamDhundho.config.JwtService;
import com.backend.KaamDhundho.repository.auth.SessionRepository;
import com.backend.KaamDhundho.repository.auth.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
@Service
public class AuthService {
    private final UserRepository users;
    private final SessionRepository sessions;
    private final PasswordEncoder encoder;

    private final JwtService jwt;
    private final int maxSessions;

    public AuthService(
            UserRepository users,
            SessionRepository sessions,
            PasswordEncoder encoder,
            JwtService jwt,
            @Value("${app.max-sessions-per-user}") int maxSessions) {
        this.users = users;
        this.sessions = sessions;
        this.encoder = encoder;
        this.jwt = jwt;
        this.maxSessions = maxSessions;
    }

    // Register method (same as before, but it now returns both tokens)
    public AuthResponse register(RegisterRequest req) {
        // Check if the email is already registered
        users.findByEmail(req.email()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already registered");
        });

        // Create a new user
        User u = User.builder()
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .role(req.role())
                .name(req.name())
                .mobileNumber(req.mobile_number())
                .dob(req.dob())
                .gender(req.gender())
                .build();

        // Save the user to the database
        User savedUser = users.save(u);

        // Generate both JWT and Refresh Token
        String accessToken = jwt.generateAccessToken(savedUser.getId(), savedUser.getRole());
        String refreshToken = jwt.generateRefreshToken(savedUser.getId());

        // Save the session and refresh token
        Instant now = Instant.now();
        Instant accessTokenExpiration = now.plusSeconds(jwt.accessMinutes * 60L);
        sessions.save(Session.builder()
                .user(savedUser)
                .token(accessToken)
                .createdAt(now)
                .expiresAt(accessTokenExpiration)
                .build());

        // Return the AuthResponse with both tokens
        return new AuthResponse(
                savedUser.getId(),
                accessToken,
                refreshToken,
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.is_email_verified()
        );
    }

    // Login method (same as before, but it now generates both tokens)
    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Issue both tokens (JWT and Refresh token)
        String accessToken = jwt.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwt.generateRefreshToken(user.getId());

        // Enforce session limit logic
        Instant now = Instant.now();
        List<Session> existingSessions = sessions.findByUserOrderByCreatedAtAsc(user);

        // If user has reached the max session limit, delete the oldest session
        if (existingSessions.size() >= maxSessions) {
            Session oldestSession = existingSessions.get(0);  // Get the oldest session
            sessions.delete(oldestSession);  // Delete the oldest session
        }

        // Save the new session
        Instant accessTokenExpiration = now.plusSeconds(jwt.accessMinutes * 60L);
        sessions.save(Session.builder()
                .user(user)
                .token(accessToken)
                .createdAt(now)
                .expiresAt(accessTokenExpiration)
                .build());

        // Return the AuthResponse with both tokens
        return new AuthResponse(
                user.getId(),
                accessToken,
                refreshToken,
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.is_email_verified()
        );
    }

    // Logout method (same as before)
    @Transactional
    public void logout(String token) {
        sessions.deleteByToken(token);
    }

    // New method to handle refresh token request
    public AuthResponse refreshToken(String refreshToken) {
        // Parse the refresh token
        Claims claims = jwt.parse(refreshToken).getBody();

        // Get user ID from the refresh token
        Long userId = claims.get("userId", Long.class);
        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Generate a new access token
        String newAccessToken = jwt.generateAccessToken(user.getId(), user.getRole());
        // Enforce session limit logic
        Instant now = Instant.now();
        List<Session> existingSessions = sessions.findByUserOrderByCreatedAtAsc(user);

        // If user has reached the max session limit, delete the oldest session
        if (existingSessions.size() >= maxSessions) {
            Session oldestSession = existingSessions.get(0);  // Get the oldest session
            sessions.delete(oldestSession);  // Delete the oldest session
        }

        // Save the new session
        Instant accessTokenExpiration = now.plusSeconds(jwt.accessMinutes * 60L);
        sessions.save(Session.builder()
                .user(user)
                .token(newAccessToken)
                .createdAt(now)
                .expiresAt(accessTokenExpiration)
                .build());

        // Return the new access token along with the refresh token
        return new AuthResponse(
                user.getId(),
                newAccessToken,
                refreshToken,  // The refresh token remains the same
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.is_email_verified()
        );
    }

}
