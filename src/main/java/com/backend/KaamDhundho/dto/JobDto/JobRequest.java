package com.backend.KaamDhundho.dto.JobDto;

import lombok.Data;

@Data
public class JobRequest {
    private String title;
    private String description;
    private String requirements;
    private Double salary;
    private Double latitude;
    private Double longitude;
}
