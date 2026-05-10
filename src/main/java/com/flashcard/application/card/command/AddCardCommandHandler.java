package com.flashcard.application.card.command;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddCardCommandHandler {

    private final CardFactory cardFactory;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    public AddCardCommandHandler(CardFactory cardFactory,
                                 CardRepository cardRepository,
                                 DeckRepository deckRepository,
                                 UserRepository userRepository) {
        this.cardFactory = cardFactory;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
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

        return saved.getId();
    }
}
