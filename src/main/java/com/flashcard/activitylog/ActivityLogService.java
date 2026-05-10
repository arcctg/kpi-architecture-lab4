package com.flashcard.activitylog;

public interface ActivityLogService {

    void logUserRegistered(Long userId, String email);

    void logDeckCreated(Long deckId, Long ownerId, String title);

    void logDeckUpdated(Long deckId, Long ownerId, String title);

    void logDeckDeleted(Long deckId, Long ownerId);

    void logCardAdded(Long cardId, Long deckId, Long ownerId, String term);

    void logCardUpdated(Long cardId, Long deckId, Long ownerId, String term);

    void logCardDeleted(Long cardId, Long deckId, Long ownerId);
}
