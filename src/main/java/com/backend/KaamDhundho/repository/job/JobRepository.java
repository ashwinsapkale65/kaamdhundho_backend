package com.backend.KaamDhundho.repository.job;

import com.backend.KaamDhundho.entity.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query(value = "SELECT * FROM jobs j " +
            "WHERE ST_DWithin(" +
            "ST_MakePoint(j.longitude, j.latitude)::geography, " +
            "ST_MakePoint(:lng, :lat)::geography, " +
            ":radiusKm * 1000)", nativeQuery = true)
    List<Job> findNearbyJobs(@Param("lat") Double lat,
                             @Param("lng") Double lng,
                             @Param("radiusKm") Double radiusKm);
}
