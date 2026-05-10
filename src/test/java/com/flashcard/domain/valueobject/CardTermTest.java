package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CardTermTest {

    @Test
    void validTerm_createsSuccessfully() {
        CardTerm term = new CardTerm("polymorphism");
        assertThat(term.value()).isEqualTo("polymorphism");
    }

    @Test
    void blankTerm_throwsDomainError() {
        assertThatThrownBy(() -> new CardTerm("  "))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void tooLongTerm_throwsDomainError() {
        assertThatThrownBy(() -> new CardTerm("a".repeat(201)))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void maxLengthTerm_succeeds() {
        CardTerm term = new CardTerm("a".repeat(200));
        assertThat(term.value()).hasSize(200);
    }
}
