package com.flashcard.application.port;

public interface TokenProvider {

    String generateToken(String subject);
}
