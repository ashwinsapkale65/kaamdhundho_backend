package com.backend.KaamDhundho.dto.JobDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobSearchResponse {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private Double salary;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private SimpleUser provider;

    @Data
    public static class SimpleUser {
        private Long id;
        private String name;
        private String email;
        private String role;
    }
}
