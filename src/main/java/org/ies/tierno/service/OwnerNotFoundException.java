package org.ies.tierno.service;

public class OwnerNotFoundException extends RuntimeException {

    public OwnerNotFoundException(long ownerId) {
        super("Owner " + ownerId + " not found");
    }
}
