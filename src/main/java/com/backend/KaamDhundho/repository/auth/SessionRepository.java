package com.backend.KaamDhundho.repository.auth;


import com.backend.KaamDhundho.entity.auth.Session;
import com.backend.KaamDhundho.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserOrderByCreatedAtAsc(User user);
    Optional<Session> findByToken(String token);
    boolean existsByTokenAndExpiresAtAfter(String token, Instant now);
    void deleteByToken(String token);
    void deleteByUserAndExpiresAtBefore(User user, Instant now);
}
