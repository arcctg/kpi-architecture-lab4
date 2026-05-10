package com.flashcard.presentation.controller;

import com.flashcard.application.dto.DeckResult;
import com.flashcard.application.deck.command.*;
import com.flashcard.application.deck.query.*;
import com.flashcard.domain.model.DomainPage;
import com.flashcard.presentation.dto.request.CreateDeckRequest;
import com.flashcard.presentation.dto.request.UpdateDeckRequest;
import com.flashcard.presentation.dto.response.DeckResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final CreateDeckCommandHandler createDeckCommandHandler;
    private final GetDeckQueryHandler getDeckQueryHandler;
    private final ListDecksQueryHandler listDecksQueryHandler;
    private final UpdateDeckCommandHandler updateDeckCommandHandler;
    private final DeleteDeckCommandHandler deleteDeckCommandHandler;

    public DeckController(CreateDeckCommandHandler createDeckCommandHandler,
                          GetDeckQueryHandler getDeckQueryHandler,
                          ListDecksQueryHandler listDecksQueryHandler,
                          UpdateDeckCommandHandler updateDeckCommandHandler,
                          DeleteDeckCommandHandler deleteDeckCommandHandler) {
        this.createDeckCommandHandler = createDeckCommandHandler;
        this.getDeckQueryHandler = getDeckQueryHandler;
        this.listDecksQueryHandler = listDecksQueryHandler;
        this.updateDeckCommandHandler = updateDeckCommandHandler;
        this.deleteDeckCommandHandler = deleteDeckCommandHandler;
    }

    @GetMapping
    public ResponseEntity<DomainPage<DeckResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        DomainPage<DeckResult> results = listDecksQueryHandler.handle(new ListDecksQuery(auth.getName(), page, size));
        DomainPage<DeckResponse> response = new DomainPage<>(
                results.content().stream().map(DeckResponse::from).toList(),
                results.page(), results.size(), results.totalElements(), results.totalPages()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DeckResponse> create(@Valid @RequestBody CreateDeckRequest request,
                                               Authentication auth) {
        Long deckId = createDeckCommandHandler.handle(new CreateDeckCommand(request.title(), request.description(), auth.getName()));
        DeckResult result = getDeckQueryHandler.handle(new GetDeckQuery(deckId, auth.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(DeckResponse.from(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> get(@PathVariable Long id, Authentication auth) {
        DeckResult result = getDeckQueryHandler.handle(new GetDeckQuery(id, auth.getName()));
        return ResponseEntity.ok(DeckResponse.from(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateDeckRequest request,
                                               Authentication auth) {
        Long deckId = updateDeckCommandHandler.handle(new UpdateDeckCommand(id, request.title(), request.description(), auth.getName()));
        DeckResult result = getDeckQueryHandler.handle(new GetDeckQuery(deckId, auth.getName()));
        return ResponseEntity.ok(DeckResponse.from(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        deleteDeckCommandHandler.handle(new DeleteDeckCommand(id, auth.getName()));
        return ResponseEntity.noContent().build();
    }
}
