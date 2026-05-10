package com.flashcard.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

class CardQueryIT extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long deckId;
    private int counter = 0;

    @BeforeEach
    void setUp() throws Exception {
        String email = "it-cq-" + System.nanoTime() + "-" + (counter++) + "@test.com";
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
                        {"title":"CQ Deck %s","description":"for card queries"}
                        """.formatted(email), authHeaders()), String.class);
        deckId = objectMapper.readTree(deckResp.getBody()).get("id").asLong();
    }

    @Test
    void shouldReturnCardById() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"query-term","definition":"query-def"}
                        """, authHeaders()), String.class);
        Long cardId = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/" + cardId, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(cardId);
        assertThat(body.get("term").asText()).isEqualTo("query-term");
        assertThat(body.get("definition").asText()).isEqualTo("query-def");
        assertThat(body.has("createdAt")).isTrue();
    }

    @Test
    void shouldReturn404ForNonExistentCard() {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/999999", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnPaginatedCardList() throws Exception {
        for (int i = 0; i < 3; i++) {
            rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                    new HttpEntity<>("""
                            {"term":"page-card-%d","definition":"def"}
                            """.formatted(i), authHeaders()), String.class);
        }

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards?page=0&size=2", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertThat(body.get("content").size()).isEqualTo(2);
        assertThat(body.get("totalElements").asInt()).isEqualTo(3);
        assertThat(body.get("totalPages").asInt()).isEqualTo(2);
    }

    @Test
    void shouldFilterCardsBySearchTerm() throws Exception {
        rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"apple","definition":"a fruit"}
                        """, authHeaders()), String.class);
        rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"banana","definition":"yellow fruit"}
                        """, authHeaders()), String.class);
        rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"application","definition":"software"}
                        """, authHeaders()), String.class);

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards?search=app", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertThat(body.get("content").size()).isEqualTo(2);
    }

    @Test
    void shouldReturnRandomCard() {
        rest.exchange("/api/decks/" + deckId + "/cards", HttpMethod.POST,
                new HttpEntity<>("""
                        {"term":"random-card","definition":"def"}
                        """, authHeaders()), String.class);

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/random", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("random-card");
    }

    @Test
    void shouldReturn204WhenDeckEmpty() {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + deckId + "/cards/random", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
