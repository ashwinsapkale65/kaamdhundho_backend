package com.backend.KaamDhundho.service.auth;

// service/SessionCleanupService.java


import com.backend.KaamDhundho.repository.auth.SessionRepository;
import com.backend.KaamDhundho.repository.auth.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SessionCleanupService {
    private final SessionRepository sessions;
    private final UserRepository users;

    public SessionCleanupService(SessionRepository sessions, UserRepository users) {
        this.sessions = sessions; this.users = users;
    }

    // Every hour: remove expired rows
    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpired() {
        var now = Instant.now();
        users.findAll().forEach(u -> sessions.deleteByUserAndExpiresAtBefore(u, now));
    }
}
