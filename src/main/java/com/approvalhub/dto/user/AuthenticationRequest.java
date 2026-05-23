package com.approvalhub.dto.user;


import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "Nom d'utilisateur requis")
        String username,

        @NotBlank(message = "Mot de passe requis")
        String password
) {}
