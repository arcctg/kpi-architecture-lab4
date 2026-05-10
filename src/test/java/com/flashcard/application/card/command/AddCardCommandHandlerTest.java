package com.flashcard.application.card.command;

import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.factory.CardFactory;
import com.flashcard.domain.model.Card;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.CardRepository;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.CardDefinition;
import com.flashcard.domain.valueobject.CardTerm;
import com.flashcard.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddCardCommandHandlerTest {

    private CardFactory cardFactory;
    private CardRepository cardRepository;
    private DeckRepository deckRepository;
    private UserRepository userRepository;
    private AddCardCommandHandler handler;

    @BeforeEach
    void setUp() {
        cardFactory = mock(CardFactory.class);
        cardRepository = mock(CardRepository.class);
        deckRepository = mock(DeckRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new AddCardCommandHandler(cardFactory, cardRepository, deckRepository, userRepository);
    }

    @Test
    void shouldAddCardAndReturnId() {
        AddCardCommand command = new AddCardCommand(5L, "term", "definition", "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(true);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));

        Card card = mock(Card.class);
        when(cardFactory.create(any(CardTerm.class), any(CardDefinition.class), eq(5L))).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(card.getId()).thenReturn(20L);

        Long resultId = handler.handle(command);

        assertEquals(20L, resultId);
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        AddCardCommand command = new AddCardCommand(5L, "term", "def", "missing@example.com");
        when(userRepository.findByEmail(new Email("missing@example.com"))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDeckNotFound() {
        AddCardCommand command = new AddCardCommand(999L, "term", "def", "user@example.com");
        User user = mock(User.class);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowWhenNotOwner() {
        AddCardCommand command = new AddCardCommand(5L, "term", "def", "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(false);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));

        assertThrows(AccessDeniedError.class, () -> handler.handle(command));
        verify(cardRepository, never()).save(any());
    }
}
