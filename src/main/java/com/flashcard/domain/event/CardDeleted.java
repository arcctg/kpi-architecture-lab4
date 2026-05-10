package com.flashcard.domain.event;

import java.time.LocalDateTime;

public record CardDeleted(
        Long cardId,
        Long deckId,
        Long ownerId,
        LocalDateTime occurredAt
) {}
