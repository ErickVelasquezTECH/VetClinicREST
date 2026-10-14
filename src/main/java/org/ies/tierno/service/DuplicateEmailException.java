package org.ies.tierno.service;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email " + email + " is already registered");
    }
}