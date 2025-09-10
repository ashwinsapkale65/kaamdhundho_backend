package com.backend.KaamDhundho.repository.auth;


import com.backend.KaamDhundho.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
