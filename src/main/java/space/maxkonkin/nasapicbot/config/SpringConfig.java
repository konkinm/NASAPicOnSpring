package space.maxkonkin.nasapicbot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import space.maxkonkin.nasapicbot.client.NasaApiClient;
import space.maxkonkin.nasapicbot.repository.NasaRowTableRepository;
import space.maxkonkin.nasapicbot.service.NasaService;
import space.maxkonkin.nasapicbot.service.TranslateService;
import space.maxkonkin.nasapicbot.service.UserService;
import space.maxkonkin.nasapicbot.web.NASAPicOnSpringBot;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import(TelegramConfig.class)
@ComponentScan(basePackages = "space.maxkonkin.nasapicbot")
@PropertySource(value = "classpath:application-${SPRING_PROFILE}.yaml", factory = YamlPropertySourceFactory.class)
public class SpringConfig {
    @Value("${translate}")
    Boolean withTranslate;

    @Autowired
    private TelegramConfig telegramConfig;

    private List<BotCommand> listOfCommands = new ArrayList<>();

    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public String getBotToken() {
        return telegramConfig.getBotToken();
    }

    @Bean
    public NASAPicOnSpringBot springWebhookBot(SetWebhook setWebhook, String botToken,
                                               NasaService nasaService, TranslateService translateService,
                                               UserService userService) throws TelegramApiException {
        NASAPicOnSpringBot bot = new NASAPicOnSpringBot(setWebhook, botToken, nasaService, translateService,
                userService);

        bot.setBotPath(telegramConfig.getBotPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setWebhook(setWebhook);

        bot.setErrorText(telegramConfig.getErrorText());

        listOfCommands.add(new BotCommand("/start","Получить описание"));
        listOfCommands.add(new BotCommand("/help","Получить описание"));
        listOfCommands.add(new BotCommand("/today","Скинуть сегодняшнюю картинку"));
        listOfCommands.add(new BotCommand("/random","Скинуть случайную картинку"));
        bot.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));

        return bot;
    }

    @Bean
    public NasaService nasaService(NasaApiClient nasaApiClient, NasaRowTableRepository nasaRepository, TranslateService translateService) {
        NasaService service = new NasaService(nasaApiClient, nasaRepository, translateService);
        service.setWithTranslate(withTranslate);
        return service;
    }
}
