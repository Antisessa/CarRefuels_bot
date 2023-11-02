package ru.antisessa.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ConsumerService {
    void consumeAnswerMessage(SendMessage sendMessage);
    void consumeAnswerMessageWithException(SendMessage sendMessage);
}
