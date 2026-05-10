package com.flashcard.application.deck.command;

public record UpdateDeckCommand(
        Long deckId,
        String title,
        String description,
        String userId
) {}
