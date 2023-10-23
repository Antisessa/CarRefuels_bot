package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.service.MainService;
import ru.antisessa.service.ProducerService;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final ProducerService producerService;

    @Override
    public void processTextMessage(Update update) {
        // TODO сделать регистрацию пользователя в БД

        String output = ""; // Переменная содержащая ответ для пользователя

        // Получаем ID чата из входящего update
        Long chatId = update.getMessage().getChatId();

        output = ("Хэллоу энд велCum то лос полосс харманос фэмели"); //TODO заглушка

        sendAnswer(output, chatId);
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);

        // Отправляем ответ в RabbitMQ очередь
        producerService.producerAnswer(sendMessage);
    }
}
