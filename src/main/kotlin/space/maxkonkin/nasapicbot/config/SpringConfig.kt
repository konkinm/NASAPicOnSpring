package space.maxkonkin.nasapicbot.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import space.maxkonkin.nasapicbot.client.NasaApiClient
import space.maxkonkin.nasapicbot.repository.NasaRowTableRepository
import space.maxkonkin.nasapicbot.service.NasaService
import space.maxkonkin.nasapicbot.service.TranslateService
import space.maxkonkin.nasapicbot.service.UserService
import space.maxkonkin.nasapicbot.web.NASAPicOnSpringBot

@Configuration
@Import(TelegramConfig::class)
@ComponentScan(basePackages = ["space.maxkonkin.nasapicbot"])
@PropertySource(value = ["classpath:application-\${SPRING_PROFILE}.yaml"], factory = YamlPropertySourceFactory::class)
class SpringConfig {
    @Value("\${translate}")
    private val withTranslate: Boolean? = null

    @Autowired
    private lateinit var telegramConfig: TelegramConfig

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
    @Throws(TelegramApiException::class)
    fun springWebhookBot(
        setWebhook: SetWebhook, botToken: String,
        nasaService: NasaService,
        userService: UserService
    ): NASAPicOnSpringBot {
        val bot = NASAPicOnSpringBot(
            setWebhook, botToken, nasaService, userService
        )
        bot.setBotPath(telegramConfig.botPath)
        bot.setBotUsername(telegramConfig.botName)
        bot.setWebhook(setWebhook)
        bot.errorText = telegramConfig.errorText
        listOfCommands.add(BotCommand("/start", "Получить описание"))
        listOfCommands.add(BotCommand("/help", "Получить описание"))
        listOfCommands.add(BotCommand("/today", "Скинуть сегодняшнюю картинку"))
        listOfCommands.add(BotCommand("/random", "Скинуть случайную картинку"))
        bot.execute(SetMyCommands(listOfCommands, BotCommandScopeDefault(), null))
        return bot
    }

    @Bean
    fun nasaService(
        nasaApiClient: NasaApiClient,
        nasaRepository: NasaRowTableRepository,
        translateService: TranslateService
    ): NasaService {
        val service = NasaService(nasaApiClient, nasaRepository, translateService)
        service.withTranslate = withTranslate
        return service
    }

    @Bean
    fun nasaRowTableRepository(): NasaRowTableRepository {
        return NasaRowTableRepository(System.getenv("TABLE_NAME"))
    }
}
