package com.flashcard.application.card.query;

import com.flashcard.application.dto.CardResult;
import com.flashcard.domain.model.DomainPage;

import java.util.Optional;

public interface CardReadRepository {
    Optional<CardResult> findByIdAndDeckIdAndUserId(Long id, Long deckId, String userId);
    DomainPage<CardResult> findAllByDeckIdAndUserId(Long deckId, String userId, String search, int page, int size);
    Optional<CardResult> findRandomByDeckIdAndUserId(Long deckId, String userId);
}
