package com.flashcard.application.dto;

import java.time.LocalDateTime;

public record DeckResult(
        Long id,
        String title,
        String description,
        int cardCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
