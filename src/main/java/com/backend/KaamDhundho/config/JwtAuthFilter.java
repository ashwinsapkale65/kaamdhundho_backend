package com.backend.KaamDhundho.config;

// config/JwtAuthFilter.java


import com.backend.KaamDhundho.repository.auth.SessionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {
    private final JwtService jwtService;
    private final SessionRepository sessions;

    public JwtAuthFilter(JwtService jwtService, SessionRepository sessions) {
        this.jwtService = jwtService; this.sessions = sessions;
    }

    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var jws = jwtService.parse(token);
                Claims claims = jws.getBody();

                // Soft-revocation: token must exist in DB and not be expired
                boolean active = sessions.existsByTokenAndExpiresAtAfter(token, Instant.now());
                if (active) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            claims.get("userId"), null, List.of()  // add roles if needed
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) { /* invalid/expired token -> no auth set */ }
        }
        chain.doFilter(req, res);
    }
}
