package com.flashcard.infrastructure.persistence.adapter;

import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.Email;
import com.flashcard.infrastructure.persistence.entity.UserEntity;
import com.flashcard.infrastructure.persistence.jpa.JpaUserRepository;
import com.flashcard.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpa;

    public UserRepositoryAdapter(JpaUserRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = jpa.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.value()).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.value());
    }
}
