package com.flashcard.application.deck.command;

import com.flashcard.activitylog.ActivityLogService;
import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.DeckTitle;
import com.flashcard.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateDeckCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateDeckCommandHandler.class);

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public UpdateDeckCommandHandler(DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    ActivityLogService activityLogService) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public Long handle(UpdateDeckCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));

        if (!deck.isOwnedBy(owner.getId())) {
            throw new AccessDeniedError("You do not own this deck");
        }

        DeckTitle title = new DeckTitle(command.title());
        deck.updateTitle(title);
        deck.updateDescription(command.description());

        Deck saved = deckRepository.save(deck);

        try {
            activityLogService.logDeckUpdated(saved.getId(), owner.getId(),
                    command.title());
        } catch (Exception e) {
            log.warn("Failed to log deck update activity", e);
        }

        return saved.getId();
    }
}
