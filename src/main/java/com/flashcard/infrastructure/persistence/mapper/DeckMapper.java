package com.flashcard.infrastructure.persistence.mapper;

import com.flashcard.domain.model.Deck;
import com.flashcard.domain.valueobject.DeckTitle;
import com.flashcard.infrastructure.persistence.entity.DeckEntity;
import com.flashcard.infrastructure.persistence.entity.UserEntity;

public class DeckMapper {

    private DeckMapper() {
    }

    public static Deck toDomain(DeckEntity entity) {
        return new Deck(
                entity.getId(),
                new DeckTitle(entity.getTitle()),
                entity.getDescription(),
                entity.getOwner().getId(),
                entity.getCards().size(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static DeckEntity toEntity(Deck domain, UserEntity owner) {
        DeckEntity entity = new DeckEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle().value());
        entity.setDescription(domain.getDescription());
        entity.setOwner(owner);
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static void updateEntity(DeckEntity entity, Deck domain) {
        entity.setTitle(domain.getTitle().value());
        entity.setDescription(domain.getDescription());
    }
}
