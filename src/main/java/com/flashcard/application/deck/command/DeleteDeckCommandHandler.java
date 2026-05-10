package com.flashcard.application.deck.command;

import com.flashcard.activitylog.ActivityLogService;
import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteDeckCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(DeleteDeckCommandHandler.class);

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public DeleteDeckCommandHandler(DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    ActivityLogService activityLogService) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public void handle(DeleteDeckCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));

        if (!deck.isOwnedBy(owner.getId())) {
            throw new AccessDeniedError("You do not own this deck");
        }

        Long deckId = deck.getId();
        deckRepository.delete(deck);

        try {
            activityLogService.logDeckDeleted(deckId, owner.getId());
        } catch (Exception e) {
            log.warn("Failed to log deck deletion activity", e);
        }
    }
}
