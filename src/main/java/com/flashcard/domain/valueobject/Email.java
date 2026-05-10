package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new DomainError("Email must not be empty");
        }
        value = value.toLowerCase().strip();
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainError("Invalid email format: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
