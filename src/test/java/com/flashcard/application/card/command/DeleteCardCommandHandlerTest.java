package com.flashcard.application.card.command;

import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.model.Card;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.CardRepository;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteCardCommandHandlerTest {

    private CardRepository cardRepository;
    private DeckRepository deckRepository;
    private UserRepository userRepository;
    private DeleteCardCommandHandler handler;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        deckRepository = mock(DeckRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new DeleteCardCommandHandler(cardRepository, deckRepository, userRepository);
    }

    @Test
    void shouldDeleteCard() {
        DeleteCardCommand command = new DeleteCardCommand(5L, 10L, "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(true);
        when(deck.getId()).thenReturn(5L);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));

        Card card = mock(Card.class);
        when(card.getDeckId()).thenReturn(5L);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        handler.handle(command);

        verify(cardRepository).delete(card);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        DeleteCardCommand command = new DeleteCardCommand(5L, 10L, "missing@example.com");
        when(userRepository.findByEmail(new Email("missing@example.com"))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
        verify(cardRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenDeckNotFound() {
        DeleteCardCommand command = new DeleteCardCommand(999L, 10L, "user@example.com");
        User user = mock(User.class);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowWhenNotOwner() {
        DeleteCardCommand command = new DeleteCardCommand(5L, 10L, "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(false);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));

        assertThrows(AccessDeniedError.class, () -> handler.handle(command));
        verify(cardRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenCardNotFound() {
        DeleteCardCommand command = new DeleteCardCommand(5L, 999L, "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(true);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowWhenCardNotInDeck() {
        DeleteCardCommand command = new DeleteCardCommand(5L, 10L, "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(true);
        when(deck.getId()).thenReturn(5L);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));

        Card card = mock(Card.class);
        when(card.getDeckId()).thenReturn(99L);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
        verify(cardRepository, never()).delete(any());
    }
}
