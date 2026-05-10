package com.flashcard.application.deck.command;

import com.flashcard.activitylog.ActivityLogService;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.factory.DeckFactory;
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
public class CreateDeckCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateDeckCommandHandler.class);

    private final DeckFactory deckFactory;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public CreateDeckCommandHandler(DeckFactory deckFactory,
                                    DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    ActivityLogService activityLogService) {
        this.deckFactory = deckFactory;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public Long handle(CreateDeckCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        DeckTitle deckTitle = new DeckTitle(command.title());
        Deck deck = deckFactory.create(deckTitle, command.description(), owner.getId());
        Deck saved = deckRepository.save(deck);

        try {
            activityLogService.logDeckCreated(saved.getId(), owner.getId(),
                    command.title());
        } catch (Exception e) {
            log.warn("Failed to log deck creation activity", e);
        }

        return saved.getId();
    }
}
