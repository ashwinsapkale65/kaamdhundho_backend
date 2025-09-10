package com.backend.KaamDhundho.entity.auth;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Session {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false, unique=true, columnDefinition="TEXT")
    private String token;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;
}
