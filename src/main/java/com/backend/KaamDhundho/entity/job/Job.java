package com.backend.KaamDhundho.entity.job;

import com.backend.KaamDhundho.entity.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String requirements;
    private Double salary;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private User provider;

    // Store lat/lng separately
    private Double latitude;
    private Double longitude;

    private LocalDateTime createdAt = LocalDateTime.now();
}
