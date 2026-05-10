package com.flashcard.application.deck.query;

import com.flashcard.application.dto.DeckResult;
import com.flashcard.domain.model.DomainPage;

import java.util.Optional;

public interface DeckReadRepository {
    Optional<DeckResult> findByIdAndUserId(Long id, String userId);
    DomainPage<DeckResult> findAllByUserId(String userId, int page, int size);
}
