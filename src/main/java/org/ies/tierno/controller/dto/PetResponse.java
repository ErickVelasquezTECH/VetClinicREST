package org.ies.tierno.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.ies.tierno.model.Pet;
import org.ies.tierno.model.Species;

import java.time.LocalDate;

public record PetResponse(
        String name,
        Species species,
        LocalDate birthDate
) {
    public static PetResponse from(Pet pet){
        return new PetResponse(
                pet.name(), pet.species(), pet.birthDate()
        );
    }
}