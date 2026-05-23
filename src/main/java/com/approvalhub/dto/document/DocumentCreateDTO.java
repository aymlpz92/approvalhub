package com.approvalhub.dto.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class DocumentCreateDTO {

    @NotBlank(message = "Titre requis")
    @Size(min = 5, max = 30, message = "Ce champ doit contenir entre 5 et 30 caractères")
    private String title;

    @NotBlank(message = "Description requise")
    @Size(min = 5, message = "Ce champ doit contenir au moins 5 caractères")
    private String description;

}
