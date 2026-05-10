package com.flashcard.domain.factory;

import com.flashcard.domain.error.DuplicateError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.model.Card;
import com.flashcard.domain.repository.CardRepository;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.valueobject.CardDefinition;
import com.flashcard.domain.valueobject.CardTerm;

public class CardFactory {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public CardFactory(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public Card create(CardTerm term, CardDefinition definition, Long deckId) {
        if (deckRepository.findById(deckId).isEmpty()) {
            throw new EntityNotFoundError("Deck not found");
        }

        if (cardRepository.existsByTermAndDeckId(term, deckId)) {
            throw new DuplicateError(
                    "Card with term '" + term.value() + "' already exists in this deck");
        }

        return Card.create(term, definition, deckId);
    }
}
