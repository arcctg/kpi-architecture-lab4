package com.flashcard.domain.event;

import java.time.LocalDateTime;

public record DeckDeleted(
        Long deckId,
        Long ownerId,
        LocalDateTime occurredAt
) {}
