package com.flashcard.application.card.query;

import com.flashcard.application.dto.CardResult;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetRandomCardQueryHandler {

    private final CardReadRepository readRepository;

    public GetRandomCardQueryHandler(CardReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public Optional<CardResult> handle(GetRandomCardQuery query) {
        return readRepository.findRandomByDeckIdAndUserId(query.deckId(), query.userId());
    }
}
