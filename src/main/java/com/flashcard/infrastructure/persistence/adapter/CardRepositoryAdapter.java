package com.flashcard.infrastructure.persistence.adapter;

import com.flashcard.domain.model.Card;
import com.flashcard.domain.model.DomainPage;
import com.flashcard.domain.repository.CardRepository;
import com.flashcard.domain.valueobject.CardTerm;
import com.flashcard.infrastructure.persistence.entity.CardEntity;
import com.flashcard.infrastructure.persistence.entity.DeckEntity;
import com.flashcard.infrastructure.persistence.jpa.JpaCardRepository;
import com.flashcard.infrastructure.persistence.jpa.JpaDeckRepository;
import com.flashcard.infrastructure.persistence.mapper.CardMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class CardRepositoryAdapter implements CardRepository {

    private final JpaCardRepository jpa;
    private final JpaDeckRepository jpaDeck;

    public CardRepositoryAdapter(JpaCardRepository jpa, JpaDeckRepository jpaDeck) {
        this.jpa = jpa;
        this.jpaDeck = jpaDeck;
    }

    @Override
    public Card save(Card card) {
        CardEntity entity;
        if (card.getId() != null) {
            entity = jpa.findById(card.getId()).orElseThrow();
            CardMapper.updateEntity(entity, card);
        } else {
            DeckEntity deck = jpaDeck.findById(card.getDeckId()).orElseThrow();
            entity = CardMapper.toEntity(card, deck);
        }
        CardEntity saved = jpa.save(entity);
        return CardMapper.toDomain(saved);
    }

    @Override
    public Optional<Card> findById(Long id) {
        return jpa.findById(id).map(CardMapper::toDomain);
    }

    @Override
    public Optional<Card> findByIdAndDeckId(Long id, Long deckId) {
        return jpa.findByIdAndDeckId(id, deckId).map(CardMapper::toDomain);
    }

    @Override
    public void delete(Card card) {
        jpa.deleteById(card.getId());
    }

    @Override
    public boolean existsByTermAndDeckId(CardTerm term, Long deckId) {
        return jpa.existsByTermAndDeckId(term.value(), deckId);
    }

    @Override
    public long countByDeckId(Long deckId) {
        return jpa.countByDeckId(deckId);
    }

    @Override
    public DomainPage<Card> findByDeckId(Long deckId, int page, int size) {
        Page<CardEntity> jpaPage = jpa.findByDeckId(deckId, PageRequest.of(page, size));
        return toDomainPage(jpaPage);
    }

    @Override
    public DomainPage<Card> findByDeckIdAndTermContaining(Long deckId, String search, int page, int size) {
        Page<CardEntity> jpaPage = jpa.findByDeckIdAndTermContainingIgnoreCase(
                deckId, search, PageRequest.of(page, size));
        return toDomainPage(jpaPage);
    }

    private DomainPage<Card> toDomainPage(Page<CardEntity> jpaPage) {
        return new DomainPage<>(
                jpaPage.getContent().stream().map(CardMapper::toDomain).toList(),
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages()
        );
    }
}
