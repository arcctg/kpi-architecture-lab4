package com.flashcard.application.deck.query;

public record GetDeckQuery(
        Long deckId,
        String userId
) {}
