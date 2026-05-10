package com.flashcard.activitylog;

import com.flashcard.domain.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityLogListener.class);

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogListener(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Async("eventExecutor")
    @EventListener
    public void onUserRegistered(UserRegistered event) {
        log.debug("Handling UserRegistered event for userId={}", event.userId());
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.USER_REGISTERED, "USER", event.userId(),
                event.userId(), "User registered: " + event.email());
        activityLogRepository.save(entry);
    }

    @Async("eventExecutor")
    @EventListener
    public void onDeckCreated(DeckCreated event) {
        log.debug("Handling DeckCreated event for deckId={}", event.deckId());
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.DECK_CREATED, "DECK", event.deckId(),
                event.ownerId(), "Deck created: " + event.title());
        activityLogRepository.save(entry);
    }

    @Async("eventExecutor")
    @EventListener
    public void onDeckUpdated(DeckUpdated event) {
        log.debug("Handling DeckUpdated event for deckId={}", event.deckId());
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.DECK_UPDATED, "DECK", event.deckId(),
                event.ownerId(), "Deck updated: " + event.newTitle());
        activityLogRepository.save(entry);
    }

    @Async("eventExecutor")
    @EventListener
    public void onDeckDeleted(DeckDeleted event) {
        log.debug("Handling DeckDeleted event for deckId={}", event.deckId());
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.DECK_DELETED, "DECK", event.deckId(),
                event.ownerId(), "Deck deleted");
        activityLogRepository.save(entry);
    }

    @Async("eventExecutor")
    @EventListener
    public void onCardAdded(CardAdded event) {
        log.debug("Handling CardAdded event for cardId={}", event.cardId());
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.CARD_ADDED, "CARD", event.cardId(),
                event.ownerId(), "Card added to deck " + event.deckId() + ": " + event.term());
        activityLogRepository.save(entry);
    }

    @Async("eventExecutor")
    @EventListener
    public void onCardUpdated(CardUpdated event) {
        log.debug("Handling CardUpdated event for cardId={}", event.cardId());
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.CARD_UPDATED, "CARD", event.cardId(),
                event.ownerId(), "Card updated in deck " + event.deckId() + ": " + event.newTerm());
        activityLogRepository.save(entry);
    }

    @Async("eventExecutor")
    @EventListener
    public void onCardDeleted(CardDeleted event) {
        log.debug("Handling CardDeleted event for cardId={}", event.cardId());
        ActivityLogEntry entry = ActivityLogEntry.create(
                ActivityLogAction.CARD_DELETED, "CARD", event.cardId(),
                event.ownerId(), "Card deleted from deck " + event.deckId());
        activityLogRepository.save(entry);
    }
}
