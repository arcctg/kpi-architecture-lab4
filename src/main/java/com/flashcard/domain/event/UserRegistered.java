package com.flashcard.domain.event;

import java.time.LocalDateTime;

public record UserRegistered(
        Long userId,
        String email,
        String displayName,
        LocalDateTime occurredAt
) {}
