package com.flashcard.application.card.query;

import com.flashcard.application.dto.CardResult;
import com.flashcard.domain.model.DomainPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListCardsQueryHandler {

    private final CardReadRepository readRepository;

    public ListCardsQueryHandler(CardReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public DomainPage<CardResult> handle(ListCardsQuery query) {
        return readRepository.findAllByDeckIdAndUserId(query.deckId(), query.userId(), query.search(), query.page(), query.size());
    }
}
