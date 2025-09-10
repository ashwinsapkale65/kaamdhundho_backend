package com.backend.KaamDhundho.repository.auth;



import com.backend.KaamDhundho.entity.auth.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
