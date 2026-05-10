package com.flashcard.application.deck.query;

public record ListDecksQuery(
        String userId,
        int page,
        int size
) {}
