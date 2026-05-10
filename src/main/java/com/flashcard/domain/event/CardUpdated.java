package com.flashcard.domain.event;

import java.time.LocalDateTime;

public record CardUpdated(
        Long cardId,
        Long deckId,
        String newTerm,
        String newDefinition,
        Long ownerId,
        LocalDateTime occurredAt
) {}
