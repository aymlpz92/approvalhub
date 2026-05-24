package com.approvalhub.service;

import com.approvalhub.domain.entity.User;
import com.approvalhub.dto.user.AuthenticationRequest;
import com.approvalhub.dto.user.AuthenticationResponse;
import com.approvalhub.dto.user.RegisterRequest;
import com.approvalhub.exception.ResourceNotFoundException;
import com.approvalhub.exception.UsernameAlreadyExists;
import com.approvalhub.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.expiration-time}")
    private long jwtExpiration;

    // Inscription d'un nouvel utilisateur
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        //Vérifie que le username n'est pas déjà utilisé
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new UsernameAlreadyExists("Username is already in use");
        }

        // Crée l'entité utilisateur avec le mot de passe hashé
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRole(registerRequest.role());

        // Persiste l'utilisateur
        userRepository.save(user);

        // Génère et retourne le token
        String jwtToken = jwtService.generateToken(user.getUsername());
        return new AuthenticationResponse(jwtToken, jwtExpiration);
    }

    // Authentification d'un utilisateur existant
    public AuthenticationResponse authenticate(AuthenticationRequest authRequest) {
        // Délègue la vérification au AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        // Charge l'utilisateur (l'authentification a réussi)
        User user = userRepository.findByUsername(authRequest.username()).orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Génère le token avec claims additionnels
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("username", user.getUsername());

        String  jwtToken = jwtService.createToken(claims, user.getUsername());
        return new AuthenticationResponse(jwtToken, jwtExpiration);
    }

    // Rafraîchissement du token (prolonge la session)
    public AuthenticationResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Token invalide");
        }

        String oldToken = authHeader.substring("Bearer ".length());
        String username = jwtService.extractUsername(oldToken);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Génère un nouveau token
        String newToken = jwtService.generateToken(user.getUsername());
        return new AuthenticationResponse(newToken, jwtExpiration);
    }

}
