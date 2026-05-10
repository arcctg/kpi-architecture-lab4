package com.flashcard.infrastructure.persistence.jpa;

import com.flashcard.infrastructure.persistence.entity.CardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaCardRepository extends JpaRepository<CardEntity, Long> {

    Page<CardEntity> findByDeckId(Long deckId, Pageable pageable);

    Page<CardEntity> findByDeckIdAndTermContainingIgnoreCase(Long deckId, String term, Pageable pageable);

    boolean existsByTermAndDeckId(String term, Long deckId);

    long countByDeckId(Long deckId);

    Optional<CardEntity> findByIdAndDeckId(Long id, Long deckId);
}
