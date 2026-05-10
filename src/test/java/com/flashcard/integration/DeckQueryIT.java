package com.flashcard.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

class DeckQueryIT extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private int counter = 0;

    @BeforeEach
    void setUp() throws Exception {
        String email = "it-dq-" + System.nanoTime() + "-" + (counter++) + "@test.com";
        String regBody = """
                {"email":"%s","displayName":"Tester","password":"Password1"}
                """.formatted(email);
        ResponseEntity<String> regResp = rest.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(regBody, jsonHeaders()), String.class);
        token = objectMapper.readTree(regResp.getBody()).get("token").asText();
    }

    @Test
    void shouldReturnDeckById() throws Exception {
        ResponseEntity<String> createResp = rest.exchange(
                "/api/decks", HttpMethod.POST,
                new HttpEntity<>("""
                        {"title":"Query Test Deck","description":"for query"}
                        """, authHeaders()), String.class);
        Long id = objectMapper.readTree(createResp.getBody()).get("id").asLong();

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/" + id, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(id);
        assertThat(body.get("title").asText()).isEqualTo("Query Test Deck");
        assertThat(body.get("description").asText()).isEqualTo("for query");
        assertThat(body.has("createdAt")).isTrue();
        assertThat(body.has("updatedAt")).isTrue();
    }

    @Test
    void shouldReturn404ForNonExistentDeck() {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks/999999", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnPaginatedDeckList() throws Exception {
        for (int i = 0; i < 3; i++) {
            rest.exchange("/api/decks", HttpMethod.POST,
                    new HttpEntity<>("""
                            {"title":"Page Deck %d","description":"test"}
                            """.formatted(i), authHeaders()), String.class);
        }

        ResponseEntity<String> resp = rest.exchange(
                "/api/decks?page=0&size=2", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertThat(body.get("content").size()).isEqualTo(2);
        assertThat(body.get("totalElements").asInt()).isEqualTo(3);
        assertThat(body.get("totalPages").asInt()).isEqualTo(2);
        assertThat(body.get("page").asInt()).isEqualTo(0);
        assertThat(body.get("size").asInt()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyListForNewUser() throws Exception {
        ResponseEntity<String> resp = rest.exchange(
                "/api/decks?page=0&size=10", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertThat(body.get("content").size()).isEqualTo(0);
        assertThat(body.get("totalElements").asInt()).isEqualTo(0);
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
