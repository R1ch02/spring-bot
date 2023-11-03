package com.application.springbot.service;

import com.application.springbot.config.BotConfig;
//import com.application.springbot.model.User;
//import com.application.springbot.model.UserRepository;
import com.application.springbot.model.User;
import com.application.springbot.model.UserRepository;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.xml.stream.events.Comment;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;

    final String ERROR_TEXT = "Error occurred: ";



    public static final String HELP_TEXT = "This bot created to learn. \n\n" +
            "You can execute commands from the main menu on the left or by typing a command: \n\n" +
            "Type /start to see welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /deletedata to delete your saved data" +
            "Type /help to see this message again";


    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList <>();
        listOfCommands.add(new BotCommand("/start","get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata","get your data source"));
        listOfCommands.add(new BotCommand("/deletedata","delete my data"));
        listOfCommands.add(new BotCommand("/help","info"));
        listOfCommands.add(new BotCommand("/settings","set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();


        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                        break;

                case "/mydata":

                        sendMessage(chatId,userRepository.findById(chatId).toString());

                        break;

                case "/deletedata":

                        KeyboardRow row = new KeyboardRow();
                        row.add(new KeyboardButton("Да"));
                        row.add(new KeyboardButton("Нет"));
                        keyboard.add(row);
                        keyboardMarkup.setKeyboard(keyboard);
                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText("Уверены, что хотите удалить все данные? " + "\n" + "Для повторной регистрации введите /start");
                        message.setReplyMarkup(keyboardMarkup);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        log.error(ERROR_TEXT + e);
                    }


                    break;

                case "/help":
                        sendMessage(chatId,HELP_TEXT);
                        break;
                case "/settings":

                        break;

                default:
                        sendMessage(chatId,"Sorry, command was not recognized");
                        log.info("Replied to user: " + messageText);
            }
        }

    }

    private void registerUser(Message message) {
        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    private void startCommandReceived(long chatId, String name){

        String answer = "Hi, " + name + ", nice to meet you!";
        sendMessage(chatId,answer);
        log.info("Replied to user: " + name);

    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }

    }
}
