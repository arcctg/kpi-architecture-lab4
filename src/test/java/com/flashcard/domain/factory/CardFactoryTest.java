package com.flashcard.domain.factory;

import com.flashcard.domain.error.DuplicateError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.model.Card;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.repository.CardRepository;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.valueobject.CardDefinition;
import com.flashcard.domain.valueobject.CardTerm;
import com.flashcard.domain.valueobject.DeckTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardFactoryTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private DeckRepository deckRepository;

    private CardFactory cardFactory;

    @BeforeEach
    void setUp() {
        cardFactory = new CardFactory(cardRepository, deckRepository);
    }

    @Test
    void create_validInput_returnsCard() {
        CardTerm term = new CardTerm("polymorphism");
        CardDefinition def = new CardDefinition("many forms");
        Deck deck = new Deck(1L, new DeckTitle("Java"), "desc", 1L, 0,
                LocalDateTime.now(), LocalDateTime.now());

        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(cardRepository.existsByTermAndDeckId(term, 1L)).thenReturn(false);

        Card card = cardFactory.create(term, def, 1L);

        assertThat(card.getTerm()).isEqualTo(term);
        assertThat(card.getDefinition()).isEqualTo(def);
        assertThat(card.getDeckId()).isEqualTo(1L);
    }

    @Test
    void create_deckNotFound_throwsEntityNotFoundError() {
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardFactory.create(
                new CardTerm("term"), new CardDefinition("def"), 999L))
                .isInstanceOf(EntityNotFoundError.class);
    }

    @Test
    void create_duplicateTerm_throwsDuplicateError() {
        CardTerm term = new CardTerm("polymorphism");
        Deck deck = new Deck(1L, new DeckTitle("Java"), "desc", 1L, 0,
                LocalDateTime.now(), LocalDateTime.now());

        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(cardRepository.existsByTermAndDeckId(term, 1L)).thenReturn(true);

        assertThatThrownBy(() -> cardFactory.create(term, new CardDefinition("def"), 1L))
                .isInstanceOf(DuplicateError.class)
                .hasMessageContaining("polymorphism");
    }
}
