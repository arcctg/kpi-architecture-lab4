package com.flashcard.activitylog;

import com.flashcard.domain.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;

class ActivityLogListenerTest {

    private ActivityLogRepository activityLogRepository;
    private ActivityLogListener listener;

    @BeforeEach
    void setUp() {
        activityLogRepository = mock(ActivityLogRepository.class);
        when(activityLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        listener = new ActivityLogListener(activityLogRepository);
    }

    @Test
    void shouldHandleUserRegistered() {
        UserRegistered event = new UserRegistered(1L, "a@b.com", "Name", LocalDateTime.now());
        listener.onUserRegistered(event);
        verify(activityLogRepository).save(argThat(e ->
                e.getAction() == ActivityLogAction.USER_REGISTERED && e.getEntityId().equals(1L)));
    }

    @Test
    void shouldHandleDeckCreated() {
        DeckCreated event = new DeckCreated(10L, "Title", "Desc", 1L, LocalDateTime.now());
        listener.onDeckCreated(event);
        verify(activityLogRepository).save(argThat(e ->
                e.getAction() == ActivityLogAction.DECK_CREATED && e.getEntityId().equals(10L)));
    }

    @Test
    void shouldHandleDeckUpdated() {
        DeckUpdated event = new DeckUpdated(10L, "New", "Desc", 1L, LocalDateTime.now());
        listener.onDeckUpdated(event);
        verify(activityLogRepository).save(argThat(e ->
                e.getAction() == ActivityLogAction.DECK_UPDATED));
    }

    @Test
    void shouldHandleDeckDeleted() {
        DeckDeleted event = new DeckDeleted(10L, 1L, LocalDateTime.now());
        listener.onDeckDeleted(event);
        verify(activityLogRepository).save(argThat(e ->
                e.getAction() == ActivityLogAction.DECK_DELETED));
    }

    @Test
    void shouldHandleCardAdded() {
        CardAdded event = new CardAdded(20L, 10L, "term", "def", 1L, LocalDateTime.now());
        listener.onCardAdded(event);
        verify(activityLogRepository).save(argThat(e ->
                e.getAction() == ActivityLogAction.CARD_ADDED && e.getEntityId().equals(20L)));
    }

    @Test
    void shouldHandleCardUpdated() {
        CardUpdated event = new CardUpdated(20L, 10L, "t", "d", 1L, LocalDateTime.now());
        listener.onCardUpdated(event);
        verify(activityLogRepository).save(argThat(e ->
                e.getAction() == ActivityLogAction.CARD_UPDATED));
    }

    @Test
    void shouldHandleCardDeleted() {
        CardDeleted event = new CardDeleted(20L, 10L, 1L, LocalDateTime.now());
        listener.onCardDeleted(event);
        verify(activityLogRepository).save(argThat(e ->
                e.getAction() == ActivityLogAction.CARD_DELETED));
    }
}
