package com.application.springbot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;


@Component
public class GenerateKeyBoard {

    public InlineKeyboardButton generateButton( String buttonText, String callBackData){
        return InlineKeyboardButton.builder().text(buttonText).callbackData(callBackData).build();
    }

    public InlineKeyboardMarkup createBaseKeyBoard(){

        List<List<InlineKeyboardButton>> rowInLine =
                List.of(List.of(generateButton("1","1"),
                        generateButton("2","2")),
                List.of(generateButton("3","3"),
                        generateButton("4","4")));
        return InlineKeyboardMarkup.builder().keyboard(rowInLine).build();

    }




}
