package com.flashcard.application.deck.command;

import com.flashcard.application.port.EventPublisher;
import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.event.DeckUpdated;
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

class UpdateDeckCommandHandlerTest {

    private DeckRepository deckRepository;
    private UserRepository userRepository;
    private EventPublisher eventPublisher;
    private UpdateDeckCommandHandler handler;

    @BeforeEach
    void setUp() {
        deckRepository = mock(DeckRepository.class);
        userRepository = mock(UserRepository.class);
        eventPublisher = mock(EventPublisher.class);
        handler = new UpdateDeckCommandHandler(deckRepository, userRepository, eventPublisher);
    }

    @Test
    void shouldUpdateDeckAndPublishEvent() {
        UpdateDeckCommand command = new UpdateDeckCommand(5L, "New Title", "New Desc", "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(true);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));
        when(deckRepository.save(deck)).thenReturn(deck);
        when(deck.getId()).thenReturn(5L);

        Long resultId = handler.handle(command);

        assertEquals(5L, resultId);
        verify(eventPublisher).publish(argThat(event -> {
            DeckUpdated e = (DeckUpdated) event;
            return e.deckId().equals(5L) && e.newTitle().equals("New Title");
        }));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UpdateDeckCommand command = new UpdateDeckCommand(5L, "Title", "Desc", "missing@example.com");
        when(userRepository.findByEmail(new Email("missing@example.com"))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenDeckNotFound() {
        UpdateDeckCommand command = new UpdateDeckCommand(999L, "Title", "Desc", "user@example.com");
        User user = mock(User.class);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowWhenNotOwner() {
        UpdateDeckCommand command = new UpdateDeckCommand(5L, "Title", "Desc", "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(false);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));
        assertThrows(AccessDeniedError.class, () -> handler.handle(command));
        verify(eventPublisher, never()).publish(any());
    }
}
