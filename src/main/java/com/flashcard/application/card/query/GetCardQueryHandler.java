package com.flashcard.application.card.query;

import com.flashcard.application.dto.CardResult;
import com.flashcard.domain.error.EntityNotFoundError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCardQueryHandler {

    private final CardReadRepository readRepository;

    public GetCardQueryHandler(CardReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public CardResult handle(GetCardQuery query) {
        return readRepository.findByIdAndDeckIdAndUserId(query.cardId(), query.deckId(), query.userId())
                .orElseThrow(() -> new EntityNotFoundError("Card not found in this deck"));
    }
}
