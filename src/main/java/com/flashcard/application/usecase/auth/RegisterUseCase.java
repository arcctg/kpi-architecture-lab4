package com.flashcard.application.usecase.auth;

import com.flashcard.application.dto.AuthResult;
import com.flashcard.application.port.PasswordEncoder;
import com.flashcard.application.port.TokenProvider;
import com.flashcard.domain.error.DuplicateError;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public RegisterUseCase(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResult execute(String email, String displayName, String password) {
        Email emailVo = new Email(email);

        if (userRepository.existsByEmail(emailVo)) {
            throw new DuplicateError("Email already registered");
        }

        String hash = passwordEncoder.encode(password);
        User user = User.create(emailVo, hash, displayName);
        userRepository.save(user);

        String token = tokenProvider.generateToken(emailVo.value());
        return new AuthResult(token);
    }
}
