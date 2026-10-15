package org.ies.tierno.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.ies.tierno.model.Species;

import java.time.LocalDate;

public record PetRequest(
        @NotBlank
        @Size(max = 50, message = "El nombre debe ser maximo 50 caracteres")
        String name,
        @NotNull
        Species species,
        @NotNull
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate birthDate
) {
}
