package com.backend.KaamDhundho.controller.job;

import com.backend.KaamDhundho.dto.JobDto.JobRequest;
import com.backend.KaamDhundho.dto.JobDto.JobSearchResponse;
import com.backend.KaamDhundho.entity.auth.User;
import com.backend.KaamDhundho.entity.job.Job;
import com.backend.KaamDhundho.repository.auth.UserRepository;
import com.backend.KaamDhundho.repository.job.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> createJob(@RequestBody JobRequest jobRequest, Principal principal) {
        try {
            User provider = userRepository.findById(Long.parseLong(principal.getName()))
                    .orElseThrow(() -> new RuntimeException("Provider not found"));

            Job job = new Job();
            job.setTitle(jobRequest.getTitle());
            job.setDescription(jobRequest.getDescription());
            job.setRequirements(jobRequest.getRequirements());
            job.setSalary(jobRequest.getSalary());
            job.setLatitude(jobRequest.getLatitude());
            job.setLongitude(jobRequest.getLongitude());
            job.setProvider(provider);

            jobRepository.save(job);

            return ResponseEntity.ok(Collections.singletonMap("message", true)); // Successfully saved
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.singletonMap("message", true)); // Something went wrong
        }
    }


    @GetMapping("/nearby")
    public List<JobSearchResponse> getNearbyJobs(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5") Double radiusKm
    ) {
        List<Job> jobs = jobRepository.findNearbyJobs(lat, lng, radiusKm);
        return jobs.stream().map(job -> {
            JobSearchResponse response = new JobSearchResponse();
            response.setId(job.getId());
            response.setTitle(job.getTitle());
            response.setDescription(job.getDescription());
            response.setRequirements(job.getRequirements());
            response.setSalary(job.getSalary());
            response.setLatitude(job.getLatitude());
            response.setLongitude(job.getLongitude());
            response.setCreatedAt(job.getCreatedAt());
            JobSearchResponse.SimpleUser simpleUser = new JobSearchResponse.SimpleUser();
            simpleUser.setId(job.getProvider().getId());
            simpleUser.setName(job.getProvider().getName());
            simpleUser.setEmail(job.getProvider().getEmail());
            simpleUser.setRole(job.getProvider().getRole());
            response.setProvider(simpleUser);
            return response;
        }).toList();
    }

}
