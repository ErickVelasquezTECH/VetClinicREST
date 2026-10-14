package org.ies.tierno.service;

public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(long ownerId, long petId) {
        super("Pet " + petId + " not found for owner " + ownerId);
    }
}