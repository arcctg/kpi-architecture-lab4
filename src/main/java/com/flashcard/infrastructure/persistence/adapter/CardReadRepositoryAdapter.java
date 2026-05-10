package com.flashcard.infrastructure.persistence.adapter;

import com.flashcard.application.card.query.CardReadRepository;
import com.flashcard.application.dto.CardResult;
import com.flashcard.domain.model.DomainPage;
import com.flashcard.infrastructure.persistence.entity.CardEntity;
import com.flashcard.infrastructure.persistence.entity.DeckEntity;
import com.flashcard.infrastructure.persistence.entity.UserEntity;
import com.flashcard.infrastructure.persistence.jpa.JpaCardRepository;
import com.flashcard.infrastructure.persistence.jpa.JpaDeckRepository;
import com.flashcard.infrastructure.persistence.jpa.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
public class CardReadRepositoryAdapter implements CardReadRepository {

    private final JpaCardRepository jpaCard;
    private final JpaDeckRepository jpaDeck;
    private final JpaUserRepository jpaUser;
    private final Random random = new Random();

    public CardReadRepositoryAdapter(JpaCardRepository jpaCard, JpaDeckRepository jpaDeck, JpaUserRepository jpaUser) {
        this.jpaCard = jpaCard;
        this.jpaDeck = jpaDeck;
        this.jpaUser = jpaUser;
    }

    private Optional<DeckEntity> getOwnedDeck(Long deckId, String userId) {
        return jpaUser.findByEmail(userId)
                .flatMap(user -> jpaDeck.findById(deckId)
                        .filter(deck -> deck.getOwner().getId().equals(user.getId())));
    }

    @Override
    public Optional<CardResult> findByIdAndDeckIdAndUserId(Long id, Long deckId, String userId) {
        return getOwnedDeck(deckId, userId)
                .flatMap(deck -> jpaCard.findByIdAndDeckId(id, deckId))
                .map(this::toResult);
    }

    @Override
    public DomainPage<CardResult> findAllByDeckIdAndUserId(Long deckId, String userId, String search, int page, int size) {
        Optional<DeckEntity> deckOpt = getOwnedDeck(deckId, userId);
        if (deckOpt.isEmpty()) {
            return new DomainPage<>(java.util.Collections.emptyList(), page, size, 0, 0);
        }

        Page<CardEntity> entities;
        if (search != null && !search.isBlank()) {
            entities = jpaCard.findByDeckIdAndTermContainingIgnoreCase(deckId, search, PageRequest.of(page, size));
        } else {
            entities = jpaCard.findByDeckId(deckId, PageRequest.of(page, size));
        }

        return new DomainPage<>(
                entities.stream().map(this::toResult).toList(),
                entities.getNumber(),
                entities.getSize(),
                entities.getTotalElements(),
                entities.getTotalPages()
        );
    }

    @Override
    public Optional<CardResult> findRandomByDeckIdAndUserId(Long deckId, String userId) {
        return getOwnedDeck(deckId, userId).flatMap(deck -> {
            long count = jpaCard.countByDeckId(deckId);
            if (count == 0) return Optional.empty();
            int idx = random.nextInt((int) count);
            Page<CardEntity> cardPage = jpaCard.findByDeckId(deckId, PageRequest.of(idx, 1));
            return cardPage.stream().findFirst().map(this::toResult);
        });
    }

    private CardResult toResult(CardEntity entity) {
        return new CardResult(
                entity.getId(),
                entity.getTerm(),
                entity.getDefinition(),
                entity.getDeck().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
