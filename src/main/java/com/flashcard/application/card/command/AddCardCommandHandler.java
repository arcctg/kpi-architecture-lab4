package com.flashcard.application.card.command;

import com.flashcard.activitylog.ActivityLogService;
import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.factory.CardFactory;
import com.flashcard.domain.model.Card;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.CardRepository;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.CardDefinition;
import com.flashcard.domain.valueobject.CardTerm;
import com.flashcard.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddCardCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(AddCardCommandHandler.class);

    private final CardFactory cardFactory;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public AddCardCommandHandler(CardFactory cardFactory,
                                 CardRepository cardRepository,
                                 DeckRepository deckRepository,
                                 UserRepository userRepository,
                                 ActivityLogService activityLogService) {
        this.cardFactory = cardFactory;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public Long handle(AddCardCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));

        if (!deck.isOwnedBy(owner.getId())) {
            throw new AccessDeniedError("You do not own this deck");
        }

        CardTerm cardTerm = new CardTerm(command.term());
        CardDefinition cardDefinition = new CardDefinition(command.definition());
        Card card = cardFactory.create(cardTerm, cardDefinition, command.deckId());
        Card saved = cardRepository.save(card);

        try {
            activityLogService.logCardAdded(saved.getId(), command.deckId(),
                    owner.getId(), command.term());
        } catch (Exception e) {
            log.warn("Failed to log card addition activity", e);
        }

        return saved.getId();
    }
}
