package com.flashcard.application.deck.command;

import com.flashcard.application.port.EventPublisher;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.event.DeckCreated;
import com.flashcard.domain.factory.DeckFactory;
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
public class CreateDeckCommandHandler {

    private final DeckFactory deckFactory;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public CreateDeckCommandHandler(DeckFactory deckFactory,
                                    DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    EventPublisher eventPublisher) {
        this.deckFactory = deckFactory;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public Long handle(CreateDeckCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        DeckTitle deckTitle = new DeckTitle(command.title());
        Deck deck = deckFactory.create(deckTitle, command.description(), owner.getId());
        Deck saved = deckRepository.save(deck);

        eventPublisher.publish(new DeckCreated(
                saved.getId(), command.title(), command.description(),
                owner.getId(), LocalDateTime.now()));

        return saved.getId();
    }
}
