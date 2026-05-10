package com.flashcard.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

class CardControllerIT extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long deckId;
    private int counter = 0;

    @BeforeEach
    void setUp() throws Exception {
        String email = "it-card-" + System.nanoTime() + "-" + (counter++) + "@test.com";
        String regBody = """
                {"email":"%s","displayName":"Tester","password":"Password1"}
                """.formatted(email);
        ResponseEntity<String> regResp = rest.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(regBody, jsonHeaders()), String.class);
        token = objectMapper.readTree(regResp.getBody()).get("token").asText();

        ResponseEntity<String> deckResp = rest.exchange(
                "/api/decks", HttpMethod.POST,
                new HttpEntity<>("""
                        {"title":"Card IT Deck %s","description":"for card tests"}
                        """.formatted(email), authHeaders()), String.class);
        deckId = objectMapper.readTree(deckResp.getBody()).get("id").asLong();
    }

    @Test
    void createCard_returns201() {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"polymorphism","definition":"many forms"}
                        """, authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).contains("polymorphism");
    }

    @Test
    void createCard_duplicateTerm_returns409() {
        String body = """
                {"term":"duplicate","definition":"def"}
                """;
        rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), String.class);

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getCard_returns200() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"get-test","definition":"def"}
                        """, authHeaders()), String.class);
        Long cardId = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/" + cardId, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("get-test");
    }

    @Test
    void listCards_returns200() {
        rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"list-item","definition":"def"}
                        """, authHeaders()), String.class);

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards?page=0&size=10", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("content");
    }

    @Test
    void updateCard_returns200() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"update-me","definition":"old"}
                        """, authHeaders()), String.class);
        Long cardId = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/" + cardId, HttpMethod.PUT,
                new HttpEntity<>("""
                        {"term":"updated","definition":"new"}
                        """, authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("updated");
    }

    @Test
    void deleteCard_returns204() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"delete-me","definition":"bye"}
                        """, authHeaders()), String.class);
        Long cardId = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/" + cardId, HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void randomCard_emptyDeck_returns204() {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/random", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void randomCard_withCards_returns200() {
        rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"random-test","definition":"def"}
                        """, authHeaders()), String.class);

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/random", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpHeaders authHeaders() {
        HttpHeaders h = jsonHeaders();
        h.setBearerAuth(token);
        return h;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
