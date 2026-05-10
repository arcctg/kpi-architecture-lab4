package com.flashcard.domain.factory;

import com.flashcard.domain.error.DuplicateError;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.valueobject.DeckTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeckFactoryTest {

    @Mock
    private DeckRepository deckRepository;

    private DeckFactory deckFactory;

    @BeforeEach
    void setUp() {
        deckFactory = new DeckFactory(deckRepository);
    }

    @Test
    void create_uniqueTitle_returnsDeck() {
        DeckTitle title = new DeckTitle("Java Basics");
        when(deckRepository.existsByTitleAndOwnerId(title, 1L)).thenReturn(false);

        Deck deck = deckFactory.create(title, "Core Java", 1L);

        assertThat(deck.getTitle()).isEqualTo(title);
        assertThat(deck.getDescription()).isEqualTo("Core Java");
        assertThat(deck.getOwnerId()).isEqualTo(1L);
    }

    @Test
    void create_duplicateTitle_throwsDuplicateError() {
        DeckTitle title = new DeckTitle("Java Basics");
        when(deckRepository.existsByTitleAndOwnerId(title, 1L)).thenReturn(true);

        assertThatThrownBy(() -> deckFactory.create(title, "Core Java", 1L))
                .isInstanceOf(DuplicateError.class)
                .hasMessageContaining("Java Basics");
    }
}
