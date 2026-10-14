package org.ies.tierno.service;

public class OwnerHasPetsException extends RuntimeException {

    public OwnerHasPetsException(long ownerId) {
        super("Owner " + ownerId + " still has pets");
    }
}