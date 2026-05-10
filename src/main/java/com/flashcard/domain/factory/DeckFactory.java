package com.flashcard.domain.factory;

import com.flashcard.domain.error.DuplicateError;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.valueobject.DeckTitle;

public class DeckFactory {

    private final DeckRepository deckRepository;

    public DeckFactory(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    public Deck create(DeckTitle title, String description, Long ownerId) {
        if (deckRepository.existsByTitleAndOwnerId(title, ownerId)) {
            throw new DuplicateError("Deck with title '" + title.value() + "' already exists");
        }

        return Deck.create(title, description, ownerId);
    }
}
