package com.flashcard.application.card.command;

public record AddCardCommand(
        Long deckId,
        String term,
        String definition,
        String userId
) {}
