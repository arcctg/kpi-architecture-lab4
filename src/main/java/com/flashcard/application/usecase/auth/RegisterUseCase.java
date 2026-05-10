package com.flashcard.application.usecase.auth;

import com.flashcard.activitylog.ActivityLogService;
import com.flashcard.application.dto.AuthResult;
import com.flashcard.application.port.PasswordEncoder;
import com.flashcard.application.port.TokenProvider;
import com.flashcard.domain.error.DuplicateError;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final ActivityLogService activityLogService;

    public RegisterUseCase(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TokenProvider tokenProvider,
                           ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public AuthResult execute(String email, String displayName, String password) {
        Email emailVo = new Email(email);

        if (userRepository.existsByEmail(emailVo)) {
            throw new DuplicateError("Email already registered");
        }

        String hash = passwordEncoder.encode(password);
        User user = User.create(emailVo, hash, displayName);
        User saved = userRepository.save(user);

        try {
            activityLogService.logUserRegistered(saved.getId(), email);
        } catch (Exception e) {
            log.warn("Failed to log user registration activity", e);
        }

        String token = tokenProvider.generateToken(emailVo.value());
        return new AuthResult(token);
    }
}
