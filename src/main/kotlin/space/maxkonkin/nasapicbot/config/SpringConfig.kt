package space.maxkonkin.nasapicbot.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.generics.TelegramClient
import space.maxkonkin.nasapicbot.client.NasaApiClient
import space.maxkonkin.nasapicbot.repository.EntityManager
import space.maxkonkin.nasapicbot.repository.NasaRowTableRepository
import space.maxkonkin.nasapicbot.repository.UserRepository
import space.maxkonkin.nasapicbot.service.NasaService
import space.maxkonkin.nasapicbot.service.TranslateService
import space.maxkonkin.nasapicbot.service.UserService
import space.maxkonkin.nasapicbot.web.NASAPicOnSpringBot

@Configuration
@Import(TelegramConfig::class)
@ComponentScan(basePackages = ["space.maxkonkin.nasapicbot"])
@PropertySource(value = ["classpath:application-\${SPRING_PROFILE}.yaml"], factory = YamlPropertySourceFactory::class)
class SpringConfig(private val telegramConfig: TelegramConfig) {
    @Value("\${translate}")
    private val withTranslate: Boolean = false

    private val listOfCommands: MutableList<BotCommand> = ArrayList()

    @Bean
    fun setWebhook(): SetWebhook {
        return SetWebhook.builder().url(telegramConfig.webhookPath).build()
    }

    @Bean
    fun getBotToken(): String {
        return telegramConfig.botToken
    }

    @Bean
    fun springWebhookBot(
        nasaService: NasaService,
        userService: UserService,
        telegramClient: TelegramClient,
    ): NASAPicOnSpringBot {
        val bot = NASAPicOnSpringBot(
            nasaService,
            userService,
            telegramConfig.errorText,
            telegramConfig.botPath,
            ::setWebhook
        )
        //telegramClient.execute(setWebhook()) // skip setting webhook
        with(listOfCommands) {
            add(BotCommand("/start", "Получить описание"))
            add(BotCommand("/help", "Получить описание"))
            add(BotCommand("/today", "Скинуть сегодняшнюю картинку"))
            add(BotCommand("/random", "Скинуть случайную картинку"))
            add(BotCommand("/schedule", "Переключить отправку сегодняшней картинки по расписанию"))
        }

        val setMyCommands: SetMyCommands? = SetMyCommands.builder()
            .commands(listOfCommands)
            .scope(BotCommandScopeDefault()) // глобально для всех
            .build()

        telegramClient.execute(setMyCommands)
        return bot
    }

    @Bean
    fun nasaService(
        nasaApiClient: NasaApiClient,
        nasaRepository: NasaRowTableRepository,
        translateService: TranslateService,
    ): NasaService = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate)

    @Bean
    fun nasaRowTableRepository(): NasaRowTableRepository = NasaRowTableRepository(System.getenv("TABLE_NAME"))

    @Bean
    fun userRepository(): UserRepository = UserRepository(
        System.getenv("USER_TABLE_NAME"),
        EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"))
    )

    @Bean
    fun httpClient(): CloseableHttpClient = HttpClients.createDefault()

    @Bean
    fun telegramClient(): TelegramClient {
        return OkHttpTelegramClient(getBotToken());
    }

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
}
