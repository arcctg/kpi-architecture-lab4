package com.flashcard.activitylog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DefaultActivityLogServiceTest {

    private ActivityLogRepository activityLogRepository;
    private DefaultActivityLogService service;

    @BeforeEach
    void setUp() {
        activityLogRepository = mock(ActivityLogRepository.class);
        when(activityLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service = new DefaultActivityLogService(activityLogRepository);
    }

    @Test
    void shouldLogUserRegistered() {
        service.logUserRegistered(1L, "user@example.com");

        verify(activityLogRepository).save(argThat(entry ->
                entry.getAction() == ActivityLogAction.USER_REGISTERED
                        && entry.getEntityType().equals("USER")
                        && entry.getEntityId().equals(1L)
                        && entry.getOwnerId().equals(1L)
                        && entry.getDetails().contains("user@example.com")
        ));
    }

    @Test
    void shouldLogDeckCreated() {
        service.logDeckCreated(10L, 1L, "My Deck");

        verify(activityLogRepository).save(argThat(entry ->
                entry.getAction() == ActivityLogAction.DECK_CREATED
                        && entry.getEntityType().equals("DECK")
                        && entry.getEntityId().equals(10L)
                        && entry.getOwnerId().equals(1L)
                        && entry.getDetails().contains("My Deck")
        ));
    }

    @Test
    void shouldLogDeckUpdated() {
        service.logDeckUpdated(10L, 1L, "Updated Deck");

        verify(activityLogRepository).save(argThat(entry ->
                entry.getAction() == ActivityLogAction.DECK_UPDATED
                        && entry.getEntityId().equals(10L)
                        && entry.getDetails().contains("Updated Deck")
        ));
    }

    @Test
    void shouldLogDeckDeleted() {
        service.logDeckDeleted(10L, 1L);

        verify(activityLogRepository).save(argThat(entry ->
                entry.getAction() == ActivityLogAction.DECK_DELETED
                        && entry.getEntityId().equals(10L)
                        && entry.getOwnerId().equals(1L)
        ));
    }

    @Test
    void shouldLogCardAdded() {
        service.logCardAdded(20L, 10L, 1L, "Hello");

        verify(activityLogRepository).save(argThat(entry ->
                entry.getAction() == ActivityLogAction.CARD_ADDED
                        && entry.getEntityType().equals("CARD")
                        && entry.getEntityId().equals(20L)
                        && entry.getDetails().contains("Hello")
        ));
    }

    @Test
    void shouldLogCardUpdated() {
        service.logCardUpdated(20L, 10L, 1L, "Updated");

        verify(activityLogRepository).save(argThat(entry ->
                entry.getAction() == ActivityLogAction.CARD_UPDATED
                        && entry.getEntityId().equals(20L)
                        && entry.getDetails().contains("Updated")
        ));
    }

    @Test
    void shouldLogCardDeleted() {
        service.logCardDeleted(20L, 10L, 1L);

        verify(activityLogRepository).save(argThat(entry ->
                entry.getAction() == ActivityLogAction.CARD_DELETED
                        && entry.getEntityId().equals(20L)
                        && entry.getOwnerId().equals(1L)
        ));
    }
}
