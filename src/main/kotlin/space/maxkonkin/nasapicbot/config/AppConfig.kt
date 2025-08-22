package space.maxkonkin.nasapicbot.config

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import org.koin.dsl.module
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.generics.TelegramClient
import space.maxkonkin.nasapicbot.client.NasaApiClient
import space.maxkonkin.nasapicbot.client.YandexTranslateApiClient
import space.maxkonkin.nasapicbot.config.PropertiesLoader.loadProperties
import space.maxkonkin.nasapicbot.repository.EntityManager
import space.maxkonkin.nasapicbot.repository.NasaRowTableRepository
import space.maxkonkin.nasapicbot.repository.UserRepository
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

    single { ObjectMapper() }
    single { httpClient() }
    single { telegramClient(get()) }
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
    single { UserService(get()) }
}

val bot = module {
    includes(nasa, user)

    single { createBotCommands(get(), get()) }
    single { setWebhook(get()) }
    single { NASAPicOnSpringBot(get(), get(), get<TelegramConfig>().errorText, get<TelegramConfig>().botPath, { get<SetWebhook>() }) }
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

fun createBotCommands(telegramConfig: TelegramConfig, telegramClient: TelegramClient) {
    val setMyCommands: SetMyCommands = SetMyCommands.builder()
        .commands(telegramConfig.listOfCommands)
        .scope(BotCommandScopeDefault()) // глобально для всех
        .build()
    telegramClient.execute(setMyCommands)
}

fun withTranslate(properties: Map<String, Any>) = properties["translate"] as Boolean