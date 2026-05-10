package com.flashcard.application.card.command;

import com.flashcard.activitylog.ActivityLogService;
import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
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
public class UpdateCardCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateCardCommandHandler.class);

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public UpdateCardCommandHandler(CardRepository cardRepository,
                                    DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    ActivityLogService activityLogService) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public Long handle(UpdateCardCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));

        if (!deck.isOwnedBy(owner.getId())) {
            throw new AccessDeniedError("You do not own this deck");
        }

        Card card = cardRepository.findById(command.cardId())
                .orElseThrow(() -> new EntityNotFoundError("Card not found"));

        if (!card.getDeckId().equals(deck.getId())) {
            throw new EntityNotFoundError("Card does not belong to this deck");
        }

        CardTerm term = new CardTerm(command.term());
        CardDefinition definition = new CardDefinition(command.definition());
        card.updateTerm(term);
        card.updateDefinition(definition);

        Card saved = cardRepository.save(card);

        try {
            activityLogService.logCardUpdated(saved.getId(), command.deckId(),
                    owner.getId(), command.term());
        } catch (Exception e) {
            log.warn("Failed to log card update activity", e);
        }

        return saved.getId();
    }
}
