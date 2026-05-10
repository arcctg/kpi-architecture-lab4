package com.flashcard.domain.model;

import com.flashcard.domain.valueobject.CardDefinition;
import com.flashcard.domain.valueobject.CardTerm;
import java.time.LocalDateTime;

public class Card {

    private Long id;
    private CardTerm term;
    private CardDefinition definition;
    private final Long deckId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Card(Long id, CardTerm term, CardDefinition definition,
                Long deckId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.term = term;
        this.definition = definition;
        this.deckId = deckId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Card create(CardTerm term, CardDefinition definition, Long deckId) {
        LocalDateTime now = LocalDateTime.now();
        return new Card(null, term, definition, deckId, now, now);
    }

    public void updateTerm(CardTerm newTerm) {
        this.term = newTerm;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDefinition(CardDefinition newDefinition) {
        this.definition = newDefinition;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean belongsToDeck(Long deckId) {
        return this.deckId.equals(deckId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CardTerm getTerm() {
        return term;
    }

    public CardDefinition getDefinition() {
        return definition;
    }

    public Long getDeckId() {
        return deckId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
