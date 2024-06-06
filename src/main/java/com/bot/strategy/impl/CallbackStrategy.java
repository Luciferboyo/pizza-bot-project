package com.bot.strategy.impl;

import com.bot.*;
import com.bot.strategy.Strategy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallbackStrategy implements Strategy {

    @Override
    public SendMessage getResponse(Update update) {

        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(CommonMessage.UNKNOWN_RESPONSE);

        String callBackResponse = update.getCallbackQuery().getData();

        if (PizzaStore.PIZZA_TYPE_LIST.contains(callBackResponse)){

            System.out.println("PIZZA_TYPE:" + callBackResponse);

            MongoDB.updateField(MongoDB.PIZZA_TYPE,callBackResponse,chatId);

            sendMessage.setText("Select the pizza size you'd like to have");

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            List<InlineKeyboardButton> buttonsRow = new ArrayList<>();

            for (Map.Entry<String,Double> set:PizzaStore.PIZZA_SIZES_MAP.entrySet()){
                InlineKeyboardButton button = new InlineKeyboardButton();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(set.getKey()).append(": $").append(set.getValue());
                button.setText(stringBuilder.toString());
                button.setCallbackData(set.getKey());
                buttonsRow.add(button);
            }


            keyboard.add(buttonsRow);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(keyboard);

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        if (PizzaStore.PIZZA_SIZES_MAP.containsKey(callBackResponse)){
            MongoDB.updateField(MongoDB.PIZZA_SIZE,callBackResponse,chatId);
            sendMessage.setText("Please select the drink you'd to have:");

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> buttonRow = new ArrayList<>();

            for (Map.Entry<String,Double> set:PizzaStore.DRINKS_MAP.entrySet()){
                InlineKeyboardButton button = new InlineKeyboardButton();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(set.getKey()).append(": $").append(set.getValue());
                button.setText(stringBuilder.toString());
                button.setCallbackData(set.getKey());
                buttonRow.add(button);
            }

            keyboard.add(buttonRow);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        if (PizzaStore.DRINKS_MAP.containsKey(callBackResponse)){

            MongoDB.updateField(MongoDB.DRINK,callBackResponse,chatId);
            MongoDB.updateField(MongoDB.ORDERS_STATE, OrderState.ADDRESS.toString(),chatId);

            sendMessage.setText("Please enter the delivery address for your pizza");

        }

        return sendMessage;
    }
}
