package com.flashcard.infrastructure.persistence.jpa;

import com.flashcard.infrastructure.persistence.entity.DeckEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDeckRepository extends JpaRepository<DeckEntity, Long> {

    Page<DeckEntity> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByTitleAndOwnerId(String title, Long ownerId);
}
