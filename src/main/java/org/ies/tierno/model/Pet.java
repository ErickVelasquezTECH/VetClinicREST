package org.ies.tierno.model;

import java.time.LocalDate;

public record Pet(Long id, Long ownerId, String name, Species species, LocalDate birthDate) {
}