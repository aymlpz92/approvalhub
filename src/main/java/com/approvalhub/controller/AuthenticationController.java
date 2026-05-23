package com.approvalhub.controller;

import com.approvalhub.dto.user.AuthenticationRequest;
import com.approvalhub.dto.user.AuthenticationResponse;
import com.approvalhub.dto.user.RegisterRequest;
import com.approvalhub.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    // Post /api/auth/register - Inscription d'un nouvel utilisateur
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    // POST /api/auth/login - connexion utilisateur existant
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    // POST /api/auth/refresh - Rafraîchissement du token
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestHeader("Autorisation") String authHeader) {
        return ResponseEntity.ok(authService.refreshToken(authHeader));
    }
}
