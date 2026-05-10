package com.flashcard.domain.repository;

import com.flashcard.domain.model.Card;
import com.flashcard.domain.model.DomainPage;
import com.flashcard.domain.valueobject.CardTerm;
import java.util.Optional;

public interface CardRepository {

    Card save(Card card);

    Optional<Card> findById(Long id);

    Optional<Card> findByIdAndDeckId(Long id, Long deckId);

    void delete(Card card);

    boolean existsByTermAndDeckId(CardTerm term, Long deckId);

    long countByDeckId(Long deckId);

    DomainPage<Card> findByDeckId(Long deckId, int page, int size);

    DomainPage<Card> findByDeckIdAndTermContaining(Long deckId, String search, int page, int size);
}
