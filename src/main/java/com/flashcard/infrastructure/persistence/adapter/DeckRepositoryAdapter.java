package com.flashcard.infrastructure.persistence.adapter;

import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.DomainPage;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.valueobject.DeckTitle;
import com.flashcard.infrastructure.persistence.entity.DeckEntity;
import com.flashcard.infrastructure.persistence.entity.UserEntity;
import com.flashcard.infrastructure.persistence.jpa.JpaDeckRepository;
import com.flashcard.infrastructure.persistence.jpa.JpaUserRepository;
import com.flashcard.infrastructure.persistence.mapper.DeckMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class DeckRepositoryAdapter implements DeckRepository {

    private final JpaDeckRepository jpa;
    private final JpaUserRepository jpaUser;

    public DeckRepositoryAdapter(JpaDeckRepository jpa, JpaUserRepository jpaUser) {
        this.jpa = jpa;
        this.jpaUser = jpaUser;
    }

    @Override
    public Deck save(Deck deck) {
        DeckEntity entity;
        if (deck.getId() != null) {
            entity = jpa.findById(deck.getId()).orElseThrow();
            DeckMapper.updateEntity(entity, deck);
        } else {
            UserEntity owner = jpaUser.findById(deck.getOwnerId()).orElseThrow();
            entity = DeckMapper.toEntity(deck, owner);
        }
        DeckEntity saved = jpa.save(entity);
        return DeckMapper.toDomain(saved);
    }

    @Override
    public Optional<Deck> findById(Long id) {
        return jpa.findById(id).map(DeckMapper::toDomain);
    }

    @Override
    public void delete(Deck deck) {
        jpa.deleteById(deck.getId());
    }

    @Override
    public boolean existsByTitleAndOwnerId(DeckTitle title, Long ownerId) {
        return jpa.existsByTitleAndOwnerId(title.value(), ownerId);
    }

    @Override
    public DomainPage<Deck> findByOwnerId(Long ownerId, int page, int size) {
        Page<DeckEntity> jpaPage = jpa.findByOwnerId(ownerId, PageRequest.of(page, size));
        return new DomainPage<>(
                jpaPage.getContent().stream().map(DeckMapper::toDomain).toList(),
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages()
        );
    }
}
