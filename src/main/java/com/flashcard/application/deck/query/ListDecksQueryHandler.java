package com.flashcard.application.deck.query;

import com.flashcard.application.dto.DeckResult;
import com.flashcard.domain.model.DomainPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListDecksQueryHandler {

    private final DeckReadRepository readRepository;

    public ListDecksQueryHandler(DeckReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public DomainPage<DeckResult> handle(ListDecksQuery query) {
        return readRepository.findAllByUserId(query.userId(), query.page(), query.size());
    }
}
