package com.flashcard.application.deck.command;

import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.factory.DeckFactory;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.DeckTitle;
import com.flashcard.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateDeckCommandHandlerTest {

    private DeckFactory deckFactory;
    private DeckRepository deckRepository;
    private UserRepository userRepository;
    private CreateDeckCommandHandler handler;

    @BeforeEach
    void setUp() {
        deckFactory = mock(DeckFactory.class);
        deckRepository = mock(DeckRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new CreateDeckCommandHandler(deckFactory, deckRepository, userRepository);
    }

    @Test
    void shouldCreateDeckAndReturnId() {
        CreateDeckCommand command = new CreateDeckCommand("Title", "Desc", "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));

        Deck deck = mock(Deck.class);
        when(deckFactory.create(any(DeckTitle.class), eq("Desc"), eq(1L))).thenReturn(deck);
        when(deckRepository.save(deck)).thenReturn(deck);
        when(deck.getId()).thenReturn(10L);

        Long resultId = handler.handle(command);

        assertEquals(10L, resultId);
        verify(deckRepository).save(deck);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        CreateDeckCommand command = new CreateDeckCommand("Title", "Desc", "missing@example.com");
        when(userRepository.findByEmail(new Email("missing@example.com"))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundError.class, () -> handler.handle(command));
        verify(deckRepository, never()).save(any());
    }
}
