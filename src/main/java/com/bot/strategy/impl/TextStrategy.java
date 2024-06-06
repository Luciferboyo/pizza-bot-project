package com.bot.strategy.impl;

import com.bot.*;
import com.bot.strategy.Strategy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TextStrategy implements Strategy {

    @Override
    public SendMessage getResponse(Update update) {

        String chatId = String.valueOf(update.getMessage().getChatId());

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);

        sendMessage.setText(CommonMessage.UNKNOWN_RESPONSE);

        String textUpdate = update.getMessage().getText().trim();

        if (textUpdate.equalsIgnoreCase("/start")){

            if (!MongoDB.userExists(chatId)){
                MongoDB.insertNewUserId(chatId);
            }

            sendMessage.setText("Welcome to Pizza Inc.Please select the pizza option you'd like to have");

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            List<InlineKeyboardButton> buttonsRow = new ArrayList<>();

            for (String pizza: PizzaStore.PIZZA_TYPE_LIST){
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(pizza);
                button.setCallbackData(pizza);
                buttonsRow.add(button);
            }

            keyboard.add(buttonsRow);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(keyboard);

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        if (MongoDB.getFieldValue(MongoDB.ORDERS_STATE, chatId).equalsIgnoreCase(OrderState.ADDRESS.toString())){
            MongoDB.updateField(MongoDB.ADDRESS,textUpdate,chatId);
            MongoDB.updateField(MongoDB.ORDERS_STATE, OrderState.PAYMENT.toString(),chatId);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The final total price to be paid is going to be:\n\n").append(" $").append(Calculator.getFinalPrice(chatId)).append("\n\nTo get back to the main menu,tap /start");
            sendMessage.setText(stringBuilder.toString());
        }

        return sendMessage;
    }
}
