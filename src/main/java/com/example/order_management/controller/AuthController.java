package com.example.order_management.controller;

import com.example.order_management.dto.LoginRequest;
import com.example.order_management.dto.LoginResponse;
import com.example.order_management.dto.RegisterRequest;
import com.example.order_management.dto.UserResponse;
import com.example.order_management.entity.UserEntity;
import com.example.order_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody RegisterRequest request){
        UserEntity user = authService.register(request.email(), request.password());
        return new UserResponse(user.getId(), user.getEmail());
    }

    @GetMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request.email(),request.password());
    }

}
