package com.flashcard.domain.repository;

import com.flashcard.domain.model.User;
import com.flashcard.domain.valueobject.Email;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
