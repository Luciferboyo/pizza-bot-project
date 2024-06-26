package com.bot;

import com.bot.strategy.impl.CallbackStrategy;
import com.bot.strategy.impl.TextStrategy;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Responder extends TelegramLongPollingBot {

    Logger LOGGER = Logger.getLogger(Responder.class.getName());

    @Override
    public String getBotUsername() {
        return Bot.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return Bot.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        try {

            SendMessage response = new SendMessage();

            if(update.hasCallbackQuery()){
                response = new CallbackStrategy().getResponse(update);
            }

            if(update.hasMessage()){

                response = new TextStrategy().getResponse(update);
            }

            if(response == null){
                LOGGER.log(Level.WARNING,"Update type could not be determined",update);
                return;
            }

            sendApiMethod(response);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
