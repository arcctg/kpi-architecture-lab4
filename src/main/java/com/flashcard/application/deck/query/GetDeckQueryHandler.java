package com.flashcard.application.deck.query;

import com.flashcard.application.dto.DeckResult;
import com.flashcard.domain.error.EntityNotFoundError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetDeckQueryHandler {

    private final DeckReadRepository readRepository;

    public GetDeckQueryHandler(DeckReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public DeckResult handle(GetDeckQuery query) {
        return readRepository.findByIdAndUserId(query.deckId(), query.userId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));
    }
}
