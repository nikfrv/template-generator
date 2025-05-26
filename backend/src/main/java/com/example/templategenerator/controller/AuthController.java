package com.example.templategenerator.controller;

import com.example.templategenerator.model.dto.db.AuthRequest;
import com.example.templategenerator.model.dto.db.AuthResponse;
import com.example.templategenerator.model.dto.db.RefreshRequest;
import com.example.templategenerator.model.dto.db.RegisterRequest;
import com.example.templategenerator.service.authentication.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (authService.existsByEmail(request.email())) {
            return ResponseEntity
                    .status(409)
                    .body(Map.of("message", "Пользователь с таким email уже существует"));
        }
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }
}
