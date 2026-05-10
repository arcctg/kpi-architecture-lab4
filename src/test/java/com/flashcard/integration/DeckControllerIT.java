package com.flashcard.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

class DeckControllerIT extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private int counter = 0;

    @BeforeEach
    void setUp() throws Exception {
        String email = "it-deck-" + System.nanoTime() + "-" + (counter++) + "@test.com";
        String regBody = """
                {"email":"%s","displayName":"Tester","password":"Password1"}
                """.formatted(email);

        ResponseEntity<String> regResp = rest.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(regBody, jsonHeaders()), String.class);
        JsonNode node = objectMapper.readTree(regResp.getBody());
        token = node.get("token").asText();
    }

    @Test
    void createDeck_returns201() {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks", HttpMethod.POST,
                new HttpEntity<>("""
                        {"title":"IT Deck","description":"test"}
                        """, authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).contains("IT Deck");
    }

    @Test
    void createDeck_duplicateTitle_returns409() {
        String body = """
                {"title":"Duplicate","description":"test"}
                """;
        rest.exchange("/api/decks", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), String.class);

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getDeck_returns200() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks", HttpMethod.POST,
                new HttpEntity<>("""
                        {"title":"Get Test","description":"test"}
                        """, authHeaders()), String.class);
        Long id = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + id, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("Get Test");
    }

    @Test
    void getDeck_notFound_returns404() {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/999999", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listDecks_returns200() {
        rest.exchange("/api/decks", HttpMethod.POST,
                new HttpEntity<>("""
                        {"title":"List Test","description":"test"}
                        """, authHeaders()), String.class);

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks?page=0&size=10", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("content");
    }

    @Test
    void updateDeck_returns200() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks", HttpMethod.POST,
                new HttpEntity<>("""
                        {"title":"Update Me","description":"old"}
                        """, authHeaders()), String.class);
        Long id = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + id, HttpMethod.PUT,
                new HttpEntity<>("""
                        {"title":"Updated","description":"new"}
                        """, authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("Updated");
    }

    @Test
    void deleteDeck_returns204() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks", HttpMethod.POST,
                new HttpEntity<>("""
                        {"title":"Delete Me","description":"bye"}
                        """, authHeaders()), String.class);
        Long id = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + id, HttpMethod.DELETE,
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
