package com.flashcard.presentation.controller;

import com.flashcard.application.dto.CardResult;
import com.flashcard.application.card.command.*;
import com.flashcard.application.card.query.*;
import com.flashcard.domain.model.DomainPage;
import com.flashcard.presentation.dto.request.CreateCardRequest;
import com.flashcard.presentation.dto.request.UpdateCardRequest;
import com.flashcard.presentation.dto.response.CardResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/decks/{deckId}/cards")
public class CardController {

    private final AddCardCommandHandler addCardCommandHandler;
    private final GetCardQueryHandler getCardQueryHandler;
    private final ListCardsQueryHandler listCardsQueryHandler;
    private final UpdateCardCommandHandler updateCardCommandHandler;
    private final DeleteCardCommandHandler deleteCardCommandHandler;
    private final GetRandomCardQueryHandler getRandomCardQueryHandler;

    public CardController(AddCardCommandHandler addCardCommandHandler,
                          GetCardQueryHandler getCardQueryHandler,
                          ListCardsQueryHandler listCardsQueryHandler,
                          UpdateCardCommandHandler updateCardCommandHandler,
                          DeleteCardCommandHandler deleteCardCommandHandler,
                          GetRandomCardQueryHandler getRandomCardQueryHandler) {
        this.addCardCommandHandler = addCardCommandHandler;
        this.getCardQueryHandler = getCardQueryHandler;
        this.listCardsQueryHandler = listCardsQueryHandler;
        this.updateCardCommandHandler = updateCardCommandHandler;
        this.deleteCardCommandHandler = deleteCardCommandHandler;
        this.getRandomCardQueryHandler = getRandomCardQueryHandler;
    }

    @GetMapping
    public ResponseEntity<DomainPage<CardResponse>> list(
            @PathVariable Long deckId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Authentication auth) {
        DomainPage<CardResult> results = listCardsQueryHandler.handle(
                new ListCardsQuery(deckId, auth.getName(), search, page, size));
        DomainPage<CardResponse> response = new DomainPage<>(
                results.content().stream().map(CardResponse::from).toList(),
                results.page(), results.size(), results.totalElements(), results.totalPages()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CardResponse> create(@PathVariable Long deckId,
                                               @Valid @RequestBody CreateCardRequest request,
                                               Authentication auth) {
        Long cardId = addCardCommandHandler.handle(new AddCardCommand(
                deckId, request.term(), request.definition(), auth.getName()));
        CardResult result = getCardQueryHandler.handle(new GetCardQuery(deckId, cardId, auth.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CardResponse.from(result));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> get(@PathVariable Long deckId,
                                            @PathVariable Long cardId,
                                            Authentication auth) {
        CardResult result = getCardQueryHandler.handle(new GetCardQuery(deckId, cardId, auth.getName()));
        return ResponseEntity.ok(CardResponse.from(result));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardResponse> update(@PathVariable Long deckId,
                                               @PathVariable Long cardId,
                                               @Valid @RequestBody UpdateCardRequest request,
                                               Authentication auth) {
        Long updatedCardId = updateCardCommandHandler.handle(new UpdateCardCommand(
                deckId, cardId, request.term(), request.definition(), auth.getName()));
        CardResult result = getCardQueryHandler.handle(new GetCardQuery(deckId, updatedCardId, auth.getName()));
        return ResponseEntity.ok(CardResponse.from(result));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long deckId,
                                       @PathVariable Long cardId,
                                       Authentication auth) {
        deleteCardCommandHandler.handle(new DeleteCardCommand(deckId, cardId, auth.getName()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/random")
    public ResponseEntity<CardResponse> random(@PathVariable Long deckId, Authentication auth) {
        Optional<CardResult> result = getRandomCardQueryHandler.handle(new GetRandomCardQuery(deckId, auth.getName()));
        return result.map(r -> ResponseEntity.ok(CardResponse.from(r)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
