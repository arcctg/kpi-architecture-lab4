package com.flashcard.presentation.dto.response;

import com.flashcard.application.dto.DeckResult;
import java.time.LocalDateTime;

public record DeckResponse(
        Long id,
        String title,
        String description,
        int cardCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DeckResponse from(DeckResult result) {
        return new DeckResponse(
                result.id(), result.title(), result.description(),
                result.cardCount(), result.createdAt(), result.updatedAt()
        );
    }
}
