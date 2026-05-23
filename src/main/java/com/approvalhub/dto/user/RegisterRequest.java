package com.approvalhub.dto.user;

import com.approvalhub.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nom d'utilisateur obligatoire")
        @Size(min = 5, max = 30, message = "Le nom d'utilisateur doit contenir entre 5 et 30 caractères")
        String username,

        @NotBlank(message = "Mot de passe obligatoire")
        @Size(min = 12,message = "Le mot de passe doit contenir au moins 12 caractères")
        String password,

        @NotBlank(message = "Vous devez confirmer le mot de passe")
        String confirmPassword,

        @NotNull(message = "Rôle obligatoire")
        Role role
) {}
