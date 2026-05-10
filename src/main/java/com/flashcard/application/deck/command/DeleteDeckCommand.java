package com.flashcard.application.deck.command;

public record DeleteDeckCommand(
        Long deckId,
        String userId
) {}
