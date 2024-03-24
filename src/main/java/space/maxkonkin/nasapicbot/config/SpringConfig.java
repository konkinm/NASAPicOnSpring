package space.maxkonkin.nasapicbot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import space.maxkonkin.nasapicbot.repository.UserDao;
import space.maxkonkin.nasapicbot.service.UserService;
import space.maxkonkin.nasapicbot.web.NASAPicOnSpringBot;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import(TelegramConfig.class)
@ComponentScan(basePackages = "space.maxkonkin.nasapicbot")
public class SpringConfig {
    @Autowired
    private TelegramConfig telegramConfig;

    private List<BotCommand> listOfCommands = new ArrayList<>();

    @Bean
    public UserService userService() {
     return new UserService(new UserDao());
    }

    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public String getBotToken() {
        return telegramConfig.getBotToken();
    }

    @Bean
    public NASAPicOnSpringBot springWebhookBot(SetWebhook setWebhook, String botToken) throws TelegramApiException {
        NASAPicOnSpringBot bot = new NASAPicOnSpringBot(setWebhook, botToken);

        bot.setBotPath(telegramConfig.getBotPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setWebhook(setWebhook);

        bot.setErrorText(telegramConfig.getErrorText());
        bot.setWithTranslate(telegramConfig.getWithTranslate());

        listOfCommands.add(new BotCommand("/start","Получить описание"));
        listOfCommands.add(new BotCommand("/help","Получить описание"));
        listOfCommands.add(new BotCommand("/give","Скинуть сегодняшнюю картинку"));
        listOfCommands.add(new BotCommand("/random","Скинуть случайную картинку"));
        bot.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));

        return bot;
    }
}
