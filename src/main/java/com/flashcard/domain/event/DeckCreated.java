package com.flashcard.domain.event;

import java.time.LocalDateTime;

public record DeckCreated(
        Long deckId,
        String title,
        String description,
        Long ownerId,
        LocalDateTime occurredAt
) {}
