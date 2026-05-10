package com.flashcard.application.card.command;

import com.flashcard.application.port.EventPublisher;
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

class UpdateCardCommandHandlerTest {

    private CardRepository cardRepository;
    private DeckRepository deckRepository;
    private UserRepository userRepository;
    private EventPublisher eventPublisher;
    private UpdateCardCommandHandler handler;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        deckRepository = mock(DeckRepository.class);
        userRepository = mock(UserRepository.class);
        eventPublisher = mock(EventPublisher.class);
        handler = new UpdateCardCommandHandler(cardRepository, deckRepository,
                userRepository, eventPublisher);
    }

    @Test
    void shouldUpdateCardAndPublishEvent() {
        UpdateCardCommand cmd = new UpdateCardCommand(5L, 10L, "new term", "new def", "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(true);
        when(deck.getId()).thenReturn(5L);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));
        Card card = mock(Card.class);
        when(card.getDeckId()).thenReturn(5L);
        when(card.getId()).thenReturn(10L);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        Long resultId = handler.handle(cmd);

        assertEquals(10L, resultId);
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UpdateCardCommand cmd = new UpdateCardCommand(5L, 10L, "t", "d", "missing@example.com");
        when(userRepository.findByEmail(new Email("missing@example.com"))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundError.class, () -> handler.handle(cmd));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenNotOwner() {
        UpdateCardCommand cmd = new UpdateCardCommand(5L, 10L, "t", "d", "user@example.com");
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail(new Email("user@example.com"))).thenReturn(Optional.of(user));
        Deck deck = mock(Deck.class);
        when(deck.isOwnedBy(1L)).thenReturn(false);
        when(deckRepository.findById(5L)).thenReturn(Optional.of(deck));
        assertThrows(AccessDeniedError.class, () -> handler.handle(cmd));
    }

    @Test
    void shouldThrowWhenCardNotInDeck() {
        UpdateCardCommand cmd = new UpdateCardCommand(5L, 10L, "t", "d", "user@example.com");
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
        assertThrows(EntityNotFoundError.class, () -> handler.handle(cmd));
    }
}
