package com.flashcard.presentation.dto.response;

import com.flashcard.application.dto.CardResult;
import java.time.LocalDateTime;

public record CardResponse(
        Long id,
        String term,
        String definition,
        Long deckId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CardResponse from(CardResult result) {
        return new CardResponse(
                result.id(), result.term(), result.definition(),
                result.deckId(), result.createdAt(), result.updatedAt()
        );
    }
}
