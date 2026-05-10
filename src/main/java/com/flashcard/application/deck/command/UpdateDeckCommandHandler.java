package com.flashcard.application.deck.command;

import com.flashcard.application.port.EventPublisher;
import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.event.DeckUpdated;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.DeckTitle;
import com.flashcard.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UpdateDeckCommandHandler {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public UpdateDeckCommandHandler(DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    EventPublisher eventPublisher) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
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

        eventPublisher.publish(new DeckUpdated(
                saved.getId(), command.title(), command.description(),
                owner.getId(), LocalDateTime.now()));

        return saved.getId();
    }
}
