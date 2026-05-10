package com.flashcard.application.dto;

import java.time.LocalDateTime;

public record CardResult(
        Long id,
        String term,
        String definition,
        Long deckId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
