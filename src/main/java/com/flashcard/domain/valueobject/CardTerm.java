package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;

public record CardTerm(String value) {

    private static final int MAX_LENGTH = 200;

    public CardTerm {
        if (value == null || value.isBlank()) {
            throw new DomainError("Card term must not be empty");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new DomainError("Card term must not exceed " + MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
