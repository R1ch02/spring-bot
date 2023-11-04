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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.xml.stream.events.Comment;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;

    private final String FIRST_QUESTION = "Что является целью криптоанализа?\n1) Увеличение количества функций замещения в криптографическом алгоритме\n2) Уменьшение количества функций подстановки в криптографическом алгоритме\n3) Определение стойкости алгоритма";
    private final String SECOND_QUESTION = "Что такое SIEM\n1) Средства предотвращения утечек данных\n2) Комплекс средств для обеспечения безопасности на предприятии\n3) Средства анализа событий безопасности";
    private final String THIRD_QUESTION = "Какие функции выполняют DLP\n1) Средства предотвращения вторжений\n2) Протокол шифрования данных\n3) Средства предотвращения утечек данных";
    private final String FOURTH_QUESTION = "Что такое IPS\n1) Протокол передачи криптографических ключей по открытым каналам\n2) Средства поиска и блокировки вторжений\n3) Средства обнаружения вредоносного ПО";

    final String ERROR_TEXT = "Error occurred: ";

    private final GenerateKeyBoard generator = new GenerateKeyBoard();

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

        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();

            if(messageText.equals("/start")){

                long chatId = update.getMessage().getChatId();
                registerUser(update.getMessage());
                sendMessage(chatId,FIRST_QUESTION,generator.createBaseKeyBoard());
            }





        } else if(update.hasCallbackQuery()){

            update.getCallbackQuery().getMessage();


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

    private void sendMessage(long chatId, String textToSend, InlineKeyboardMarkup keyboardMarkup){
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(keyboardMarkup);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }

    }
}
