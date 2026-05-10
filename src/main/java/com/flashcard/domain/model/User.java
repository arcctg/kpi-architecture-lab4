package com.flashcard.domain.model;

import com.flashcard.domain.valueobject.Email;
import java.time.LocalDateTime;

public class User {

    private final Long id;
    private final Email email;
    private final String passwordHash;
    private final String displayName;
    private final LocalDateTime createdAt;

    public User(Long id, Email email, String passwordHash,
                String displayName, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public static User create(Email email, String passwordHash, String displayName) {
        return new User(null, email, passwordHash, displayName, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
