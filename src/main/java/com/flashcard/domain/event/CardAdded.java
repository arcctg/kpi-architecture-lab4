package com.flashcard.domain.event;

import java.time.LocalDateTime;

public record CardAdded(
        Long cardId,
        Long deckId,
        String term,
        String definition,
        Long ownerId,
        LocalDateTime occurredAt
) {}
