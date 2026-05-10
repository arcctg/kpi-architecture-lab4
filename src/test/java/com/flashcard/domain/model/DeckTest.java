package com.flashcard.domain.model;

import com.flashcard.domain.valueobject.DeckTitle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class DeckTest {

    @Test
    void create_setsFieldsCorrectly() {
        Deck deck = Deck.create(new DeckTitle("Java"), "Core concepts", 1L);
        assertThat(deck.getId()).isNull();
        assertThat(deck.getTitle().value()).isEqualTo("Java");
        assertThat(deck.getDescription()).isEqualTo("Core concepts");
        assertThat(deck.getOwnerId()).isEqualTo(1L);
        assertThat(deck.getCardCount()).isZero();
    }

    @Test
    void updateTitle_changesTitle() {
        Deck deck = Deck.create(new DeckTitle("Old"), "desc", 1L);
        LocalDateTime before = deck.getUpdatedAt();

        deck.updateTitle(new DeckTitle("New"));

        assertThat(deck.getTitle().value()).isEqualTo("New");
        assertThat(deck.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void updateDescription_changesDescription() {
        Deck deck = Deck.create(new DeckTitle("Title"), "old desc", 1L);

        deck.updateDescription("new desc");

        assertThat(deck.getDescription()).isEqualTo("new desc");
    }

    @Test
    void isOwnedBy_returnsTrueForOwner() {
        Deck deck = Deck.create(new DeckTitle("Title"), "desc", 42L);
        assertThat(deck.isOwnedBy(42L)).isTrue();
    }

    @Test
    void isOwnedBy_returnsFalseForOther() {
        Deck deck = Deck.create(new DeckTitle("Title"), "desc", 42L);
        assertThat(deck.isOwnedBy(99L)).isFalse();
    }
}
