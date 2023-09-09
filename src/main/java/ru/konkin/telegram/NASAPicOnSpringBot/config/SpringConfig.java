package ru.konkin.telegram.NASAPicOnSpringBot.config;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import ru.konkin.telegram.NASAPicOnSpringBot.model.UserObject;
import ru.konkin.telegram.NASAPicOnSpringBot.repo.UserRepo;
import ru.konkin.telegram.NASAPicOnSpringBot.service.NASAPicOnSpringBot;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    private final TelegramConfig telegramConfig;

    private List<BotCommand> listOfCommands = new ArrayList<>();

    @Bean
    public UserRepo userRepo(List<UserObject> users){
     return new UserRepo(users);
    }

    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
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
