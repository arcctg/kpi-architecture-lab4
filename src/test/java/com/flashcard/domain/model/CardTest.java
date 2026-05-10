package com.flashcard.domain.model;

import com.flashcard.domain.valueobject.CardDefinition;
import com.flashcard.domain.valueobject.CardTerm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CardTest {

    @Test
    void create_setsFieldsCorrectly() {
        Card card = Card.create(new CardTerm("term"), new CardDefinition("def"), 1L);
        assertThat(card.getId()).isNull();
        assertThat(card.getTerm().value()).isEqualTo("term");
        assertThat(card.getDefinition().value()).isEqualTo("def");
        assertThat(card.getDeckId()).isEqualTo(1L);
    }

    @Test
    void updateTerm_changesTerm() {
        Card card = Card.create(new CardTerm("old"), new CardDefinition("def"), 1L);

        card.updateTerm(new CardTerm("new"));

        assertThat(card.getTerm().value()).isEqualTo("new");
    }

    @Test
    void updateDefinition_changesDefinition() {
        Card card = Card.create(new CardTerm("term"), new CardDefinition("old"), 1L);

        card.updateDefinition(new CardDefinition("new"));

        assertThat(card.getDefinition().value()).isEqualTo("new");
    }

    @Test
    void belongsToDeck_returnsTrueForCorrectDeck() {
        Card card = Card.create(new CardTerm("t"), new CardDefinition("d"), 10L);
        assertThat(card.belongsToDeck(10L)).isTrue();
    }

    @Test
    void belongsToDeck_returnsFalseForOtherDeck() {
        Card card = Card.create(new CardTerm("t"), new CardDefinition("d"), 10L);
        assertThat(card.belongsToDeck(99L)).isFalse();
    }
}
