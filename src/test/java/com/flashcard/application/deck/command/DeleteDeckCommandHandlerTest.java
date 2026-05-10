package com.flashcard.application.deck.command;

import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteDeckCommandHandlerTest {

    private DeckRepository deckRepository;
    private UserRepository userRepository;
    private DeleteDeckCommandHandler handler;

    @BeforeEach
    void setUp() {
        deckRepository = mock(DeckRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new DeleteDeckCommandHandler(deckRepository, userRepository);
    }

    @Test
    void shouldDeleteDeck() {
        DeleteDeckCommand command = new DeleteDeckCommand(5L, "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(true);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));

        handler.handle(command);

        verify(deckRepository).delete(deck);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        DeleteDeckCommand command = new DeleteDeckCommand(5L, "missing@example.com");
        when(userRepository.findByEmail(new Email("missing@example.com"))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
        verify(deckRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenDeckNotFound() {
        DeleteDeckCommand command = new DeleteDeckCommand(999L, "user@example.com");
        User user = mock(User.class);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowWhenNotOwner() {
        DeleteDeckCommand command = new DeleteDeckCommand(5L, "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(false);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));

        assertThrows(AccessDeniedError.class, () -> handler.handle(command));
        verify(deckRepository, never()).delete(any());
    }
}
