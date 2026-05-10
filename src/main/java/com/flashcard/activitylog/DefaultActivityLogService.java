package com.flashcard.activitylog;

import org.springframework.stereotype.Service;

@Service
public class DefaultActivityLogService implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public DefaultActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public void logUserRegistered(Long userId, String email) {
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.USER_REGISTERED, "USER", userId, userId,
                "User registered: " + email);
        activityLogRepository.save(entry);
    }

    @Override
    public void logDeckCreated(Long deckId, Long ownerId, String title) {
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.DECK_CREATED, "DECK", deckId, ownerId,
                "Deck created: " + title);
        activityLogRepository.save(entry);
    }

    @Override
    public void logDeckUpdated(Long deckId, Long ownerId, String title) {
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.DECK_UPDATED, "DECK", deckId, ownerId,
                "Deck updated: " + title);
        activityLogRepository.save(entry);
    }

    @Override
    public void logDeckDeleted(Long deckId, Long ownerId) {
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.DECK_DELETED, "DECK", deckId, ownerId,
                "Deck deleted");
        activityLogRepository.save(entry);
    }

    @Override
    public void logCardAdded(Long cardId, Long deckId, Long ownerId, String term) {
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.CARD_ADDED, "CARD", cardId, ownerId,
                "Card added to deck " + deckId + ": " + term);
        activityLogRepository.save(entry);
    }

    @Override
    public void logCardUpdated(Long cardId, Long deckId, Long ownerId, String term) {
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.CARD_UPDATED, "CARD", cardId, ownerId,
                "Card updated in deck " + deckId + ": " + term);
        activityLogRepository.save(entry);
    }

    @Override
    public void logCardDeleted(Long cardId, Long deckId, Long ownerId) {
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.CARD_DELETED, "CARD", cardId, ownerId,
                "Card deleted from deck " + deckId);
        activityLogRepository.save(entry);
    }
}
