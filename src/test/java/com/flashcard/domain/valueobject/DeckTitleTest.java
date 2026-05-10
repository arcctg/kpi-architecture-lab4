package com.flashcard.domain.valueobject;

import com.flashcard.domain.error.DomainError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DeckTitleTest {

    @Test
    void validTitle_createsSuccessfully() {
        DeckTitle title = new DeckTitle("Java Basics");
        assertThat(title.value()).isEqualTo("Java Basics");
    }

    @Test
    void title_stripped() {
        DeckTitle title = new DeckTitle("  Java Basics  ");
        assertThat(title.value()).isEqualTo("Java Basics");
    }

    @Test
    void blankTitle_throwsDomainError() {
        assertThatThrownBy(() -> new DeckTitle("  "))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void nullTitle_throwsDomainError() {
        assertThatThrownBy(() -> new DeckTitle(null))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void tooLongTitle_throwsDomainError() {
        String longTitle = "a".repeat(101);
        assertThatThrownBy(() -> new DeckTitle(longTitle))
                .isInstanceOf(DomainError.class);
    }

    @Test
    void maxLengthTitle_succeeds() {
        String title = "a".repeat(100);
        DeckTitle deckTitle = new DeckTitle(title);
        assertThat(deckTitle.value()).hasSize(100);
    }
}
