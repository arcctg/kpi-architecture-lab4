package com.flashcard.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

class AuthControllerIT extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private int counter = 0;

    private String uniqueEmail() {
        return "it-auth-" + System.nanoTime() + "-" + (counter++) + "@test.com";
    }

    @Test
    void register_returns201WithToken() {
        String email = uniqueEmail();
        String body = """
                {"email":"%s","displayName":"Test","password":"Password1"}
                """.formatted(email);

        ResponseEntity<String> response = rest.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("token");
    }

    @Test
    void register_duplicateEmail_returns409() {
        String email = uniqueEmail();
        String body = """
                {"email":"%s","displayName":"Test","password":"Password1"}
                """.formatted(email);

        rest.exchange("/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), String.class);

        ResponseEntity<String> response = rest.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_validCredentials_returns200WithToken() {
        String email = uniqueEmail();
        String regBody = """
                {"email":"%s","displayName":"Test","password":"Password1"}
                """.formatted(email);
        rest.exchange("/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(regBody, jsonHeaders()), String.class);

        String loginBody = """
                {"email":"%s","password":"Password1"}
                """.formatted(email);
        ResponseEntity<String> response = rest.exchange(
                "/api/auth/login", HttpMethod.POST,
                new HttpEntity<>(loginBody, jsonHeaders()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("token");
    }

    @Test
    void login_invalidPassword_returns400() {
        String email = uniqueEmail();
        String regBody = """
                {"email":"%s","displayName":"Test","password":"Password1"}
                """.formatted(email);
        rest.exchange("/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(regBody, jsonHeaders()), String.class);

        String loginBody = """
                {"email":"%s","password":"WrongPassword1"}
                """.formatted(email);
        ResponseEntity<String> response = rest.exchange(
                "/api/auth/login", HttpMethod.POST,
                new HttpEntity<>(loginBody, jsonHeaders()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
