package com.flashcard.application.card.query;

public record GetCardQuery(
        Long deckId,
        Long cardId,
        String userId
) {}
