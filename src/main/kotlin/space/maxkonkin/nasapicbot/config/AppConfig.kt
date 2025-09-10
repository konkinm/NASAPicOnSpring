package space.maxkonkin.nasapicbot.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import org.koin.dsl.module
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.generics.TelegramClient
import space.maxkonkin.nasapicbot.client.NasaApiClient
import space.maxkonkin.nasapicbot.client.YandexCloudClient
import space.maxkonkin.nasapicbot.client.YandexTranslateApiClient
import space.maxkonkin.nasapicbot.config.PropertiesLoader.loadProperties
import space.maxkonkin.nasapicbot.repository.EntityManager
import space.maxkonkin.nasapicbot.repository.NasaRowTableRepository
import space.maxkonkin.nasapicbot.repository.UserRepository
import space.maxkonkin.nasapicbot.service.MessageService
import space.maxkonkin.nasapicbot.service.NasaService
import space.maxkonkin.nasapicbot.service.TranslateService
import space.maxkonkin.nasapicbot.service.UserService
import space.maxkonkin.nasapicbot.web.NASAPicOnSpringBot

val config = module {
    single { loadProperties() }
    single { loadTelegramConfig(get()) }
    single { loadYandexTranslateConfig(get()) }
    single { loadNasaAPIConfig(get()) }
}

val utils = module {
    includes(config)

    single { 
        ObjectMapper().apply {
            registerKotlinModule()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    single { httpClient() }
    single { telegramClient(get()) }
    single { MessageService() }
}

val translate = module {
    includes(utils)

    single { YandexTranslateApiClient(get(), get(), get()) }
    single { TranslateService(get()) }
}

val nasa = module {
    includes(translate)

    single { nasaRowTableRepository() }
    single { NasaApiClient(get(), get(), get()) }
    single { NasaService(get(), get(), get(), withTranslate(get()))}
}

val user = module {
    single { userRepository() }
    single { YandexCloudClient(get(), get(), get()) }
    single { UserService(get(), get()) }
}

val bot = module {
    includes(nasa, user)

    single { botCommands(get()) }
    single { setWebhook(get()) }
    single { NASAPicOnSpringBot(get(), get(), get(), get<TelegramConfig>().botPath, get<TelegramConfig>().botName, { get<SetWebhook>() }) }
}

fun setWebhook(telegramConfig: TelegramConfig): SetWebhook =
    SetWebhook.builder().url(telegramConfig.webhookPath).build()

fun telegramClient(telegramConfig: TelegramConfig): TelegramClient =
    OkHttpTelegramClient(telegramConfig.botToken)

fun nasaRowTableRepository(): NasaRowTableRepository = NasaRowTableRepository(System.getenv("TABLE_NAME"))

fun userRepository(): UserRepository = UserRepository(
    System.getenv("USER_TABLE_NAME"),
    EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"))
)

fun httpClient(): OkHttpClient = OkHttpClient.Builder().build()

fun botCommands(telegramConfig: TelegramConfig): SetMyCommands {
    return SetMyCommands.builder()
        .commands(telegramConfig.listOfCommands)
        .scope(BotCommandScopeDefault()) // глобально для всех
        .build()
}

fun withTranslate(properties: Map<String, Any>) = properties["translate"] as Boolean