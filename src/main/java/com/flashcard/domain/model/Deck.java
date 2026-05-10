package com.flashcard.domain.model;

import com.flashcard.domain.valueobject.DeckTitle;
import java.time.LocalDateTime;

public class Deck {

    private Long id;
    private DeckTitle title;
    private String description;
    private final Long ownerId;
    private int cardCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Deck(Long id, DeckTitle title, String description, Long ownerId,
                int cardCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.cardCount = cardCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Deck create(DeckTitle title, String description, Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return new Deck(null, title, description, ownerId, 0, now, now);
    }

    public void updateTitle(DeckTitle newTitle) {
        this.title = newTitle;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOwnedBy(Long userId) {
        return this.ownerId.equals(userId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeckTitle getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public int getCardCount() {
        return cardCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
