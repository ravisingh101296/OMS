package com.example.order_management.service;

import com.example.order_management.dto.LoginRequest;
import com.example.order_management.dto.LoginResponse;
import com.example.order_management.entity.UserEntity;
import com.example.order_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String email, String rawPassword){

        String normalizedEmail = email.trim()
                .toLowerCase(Locale.ROOT);

        UserEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new LoginResponse(token, "Bearer", 900);

    }


    @Transactional
    public UserEntity register(String email, String rawPassword){

        String normalizedEmail = email.trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        UserEntity user = new UserEntity(
                normalizedEmail,
                passwordHash
        );

        return userRepository.save(user);
    }
}
