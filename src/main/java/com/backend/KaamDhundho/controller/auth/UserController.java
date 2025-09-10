package com.backend.KaamDhundho.controller.auth;


import com.backend.KaamDhundho.dto.AuthDto.UserResponse;
import com.backend.KaamDhundho.repository.auth.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository users;
    public UserController(UserRepository users) { this.users = users; }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) return ResponseEntity.status(401).build();
        Long userId = Long.valueOf(auth.getPrincipal().toString());
        return users.findById(userId)
                .map(u -> ResponseEntity.ok(new UserResponse(u)))
                .orElse(ResponseEntity.status(404).build());
    }
}
