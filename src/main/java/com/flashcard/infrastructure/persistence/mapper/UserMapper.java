package com.flashcard.infrastructure.persistence.mapper;

import com.flashcard.domain.model.User;
import com.flashcard.domain.valueobject.Email;
import com.flashcard.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                new Email(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getDisplayName(),
                entity.getCreatedAt()
        );
    }

    public static UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail().value());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setDisplayName(domain.getDisplayName());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
