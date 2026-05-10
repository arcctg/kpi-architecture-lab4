package com.flashcard.infrastructure.persistence.adapter;

import com.flashcard.application.deck.query.DeckReadRepository;
import com.flashcard.application.dto.DeckResult;
import com.flashcard.domain.model.DomainPage;
import com.flashcard.infrastructure.persistence.entity.DeckEntity;
import com.flashcard.infrastructure.persistence.entity.UserEntity;
import com.flashcard.infrastructure.persistence.jpa.JpaDeckRepository;
import com.flashcard.infrastructure.persistence.jpa.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DeckReadRepositoryAdapter implements DeckReadRepository {

    private final JpaDeckRepository jpaDeck;
    private final JpaUserRepository jpaUser;

    public DeckReadRepositoryAdapter(JpaDeckRepository jpaDeck, JpaUserRepository jpaUser) {
        this.jpaDeck = jpaDeck;
        this.jpaUser = jpaUser;
    }

    @Override
    public Optional<DeckResult> findByIdAndUserId(Long id, String userId) {
        return jpaUser.findByEmail(userId)
                .flatMap(user -> jpaDeck.findById(id)
                        .filter(deck -> deck.getOwner().getId().equals(user.getId()))
                        .map(this::toResult));
    }

    @Override
    public DomainPage<DeckResult> findAllByUserId(String userId, int page, int size) {
        Optional<UserEntity> userOpt = jpaUser.findByEmail(userId);
        if (userOpt.isEmpty()) {
            return new DomainPage<>(java.util.Collections.emptyList(), page, size, 0, 0);
        }
        Page<DeckEntity> entities = jpaDeck.findByOwnerId(userOpt.get().getId(), PageRequest.of(page, size));
        return new DomainPage<>(
                entities.stream().map(this::toResult).toList(),
                entities.getNumber(),
                entities.getSize(),
                entities.getTotalElements(),
                entities.getTotalPages()
        );
    }

    private DeckResult toResult(DeckEntity entity) {
        return new DeckResult(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCards().size(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
