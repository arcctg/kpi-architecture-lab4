package com.flashcard.application.port;

public interface EventPublisher {

    void publish(Object event);
}
