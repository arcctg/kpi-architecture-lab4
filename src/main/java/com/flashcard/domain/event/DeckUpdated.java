package com.flashcard.domain.event;

import java.time.LocalDateTime;

public record DeckUpdated(
        Long deckId,
        String newTitle,
        String newDescription,
        Long ownerId,
        LocalDateTime occurredAt
) {}
