package ru.antisessa.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Log4j
@Component
public class TelegramBot extends TelegramWebhookBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.uri}")
    private String botUri;

    private UpdateProcessor updateProcessor;

    public TelegramBot(UpdateProcessor updateProcessor){
        this.updateProcessor = updateProcessor;
    }

    @PostConstruct
    public void init(){
        updateProcessor.registerBot(this);

        try {
            SetWebhook setWebHook = SetWebhook.builder()
                    .url(botUri)
                    .build();
            this.setWebhook(setWebHook);
        } catch (TelegramApiException e) {
            log.fatal("Error Webhook link" + e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    // Итоговый путь куда будут приходить update
    // https:// + bot.uri + /callback + /update
    @Override
    public String getBotPath() {
        return "/update";
    }

    //Метод для отправки исходящих сообщений
    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

}
