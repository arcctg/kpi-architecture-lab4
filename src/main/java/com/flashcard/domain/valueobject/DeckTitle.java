package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;

public record DeckTitle(String value) {

    private static final int MAX_LENGTH = 100;

    public DeckTitle {
        if (value == null || value.isBlank()) {
            throw new DomainError("Deck title must not be empty");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new DomainError("Deck title must not exceed " + MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
