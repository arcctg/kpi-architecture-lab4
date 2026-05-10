package com.flashcard.application.card.query;

public record GetRandomCardQuery(
        Long deckId,
        String userId
) {}
