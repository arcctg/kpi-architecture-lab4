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
import com.flashcard.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteCardCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(DeleteCardCommandHandler.class);

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public DeleteCardCommandHandler(CardRepository cardRepository,
                                    DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    ActivityLogService activityLogService) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public void handle(DeleteCardCommand command) {
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

        Long cardId = card.getId();
        cardRepository.delete(card);

        try {
            activityLogService.logCardDeleted(cardId, command.deckId(), owner.getId());
        } catch (Exception e) {
            log.warn("Failed to log card deletion activity", e);
        }
    }
}
