package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void validEmail_createsSuccessfully() {
        Email email = new Email("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void email_normalizedToLowercase() {
        Email email = new Email("User@Example.COM");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void email_stripped() {
        Email email = new Email("  user@example.com  ");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void equalEmails_areEqual() {
        Email a = new Email("user@example.com");
        Email b = new Email("USER@Example.COM");
        assertThat(a).isEqualTo(b);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid", "no-at-sign.com", "@no-local.com", "no-domain@"})
    void invalidEmail_throwsDomainError(String input) {
        assertThatThrownBy(() -> new Email(input))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void nullEmail_throwsDomainError() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(DomainError.class);
    }
}
