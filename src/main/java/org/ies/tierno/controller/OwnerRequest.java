package org.ies.tierno.controller;

import jakarta.validation.constraints.*;

public record OwnerRequest(
        @NotBlank
        @Size(max = 100, message = "El nombre tiene que tener un maximo de 100 caracteres")
        String name,
        @NotBlank
        @Email
        String email,
        @NotNull
        @Pattern(regexp = "^[6789]\\d{8}$", message = "Tienen que ser 9 numeros, empezando por 6,7,8 o 9")
        String phone
) {
}

