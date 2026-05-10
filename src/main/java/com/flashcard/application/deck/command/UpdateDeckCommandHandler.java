package com.flashcard.application.deck.command;

import com.flashcard.domain.error.AccessDeniedError;
import com.flashcard.domain.error.EntityNotFoundError;
import com.flashcard.domain.model.Deck;
import com.flashcard.domain.model.User;
import com.flashcard.domain.repository.DeckRepository;
import com.flashcard.domain.repository.UserRepository;
import com.flashcard.domain.valueobject.DeckTitle;
import com.flashcard.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateDeckCommandHandler {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    public UpdateDeckCommandHandler(DeckRepository deckRepository,
                                    UserRepository userRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
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
        return saved.getId();
    }
}
