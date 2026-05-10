package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;

public record CardDefinition(String value) {

    private static final int MAX_LENGTH = 500;

    public CardDefinition {
        if (value == null || value.isBlank()) {
            throw new DomainError("Card definition must not be empty");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new DomainError("Card definition must not exceed " + MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
