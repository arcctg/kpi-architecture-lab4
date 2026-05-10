package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CardDefinitionTest {

    @Test
    void validDefinition_createsSuccessfully() {
        CardDefinition def = new CardDefinition("A form of abstraction");
        assertThat(def.value()).isEqualTo("A form of abstraction");
    }

    @Test
    void blankDefinition_throwsDomainError() {
        assertThatThrownBy(() -> new CardDefinition(""))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void tooLongDefinition_throwsDomainError() {
        assertThatThrownBy(() -> new CardDefinition("a".repeat(501)))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void maxLengthDefinition_succeeds() {
        CardDefinition def = new CardDefinition("a".repeat(500));
        assertThat(def.value()).hasSize(500);
    }
}
