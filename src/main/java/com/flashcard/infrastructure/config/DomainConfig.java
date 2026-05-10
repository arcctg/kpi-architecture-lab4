package com.flashcard.infrastructure.config;

import com.flashcard.domain.factory.CardFactory;
import com.flashcard.domain.factory.DeckFactory;
import com.flashcard.domain.repository.CardRepository;
import com.flashcard.domain.repository.DeckRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public DeckFactory deckFactory(DeckRepository deckRepository) {
        return new DeckFactory(deckRepository);
    }

    @Bean
    public CardFactory cardFactory(CardRepository cardRepository, DeckRepository deckRepository) {
        return new CardFactory(cardRepository, deckRepository);
    }
}
