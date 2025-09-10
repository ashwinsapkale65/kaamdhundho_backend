package com.backend.KaamDhundho.controller.auth;



import com.backend.KaamDhundho.dto.AuthDto.AuthResponse;
import com.backend.KaamDhundho.dto.AuthDto.LoginRequest;
import com.backend.KaamDhundho.dto.AuthDto.RegisterRequest;
import com.backend.KaamDhundho.service.auth.AuthService;
import com.backend.KaamDhundho.service.auth.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final UserService userService;


    public AuthController(AuthService auth, UserService userService) {
        this.auth = auth;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest req) {
        AuthResponse u = auth.register(req);  // Register the user
        return ResponseEntity.ok(u);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        AuthResponse authResponse = auth.login(req);  // Get the AuthResponse after login

        // Return the AuthResponse directly
        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            auth.logout(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, String>> sendVerification(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String token = authHeader.substring(7);
        Long userId = userService.extractUserIdFromToken(token);
        userService.sendVerificationEmail(userId);
        return ResponseEntity.ok(Map.of("message", "Verification email sent!"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam("token") String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified!"));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        try {
            return ResponseEntity.ok(auth.refreshToken(refreshToken));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body(null); // or custom error response
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }


}
