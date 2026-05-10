package com.flashcard.application.card.command;

public record UpdateCardCommand(
        Long deckId,
        Long cardId,
        String term,
        String definition,
        String userId
) {}
