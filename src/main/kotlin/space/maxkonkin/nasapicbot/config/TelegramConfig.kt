package space.maxkonkin.nasapicbot.config

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand

fun loadTelegramConfig(properties: Map<String, Any>): TelegramConfig {

    return TelegramConfig(
        botPath = requireNotNull((properties["telegram"] as Map<*, *>)["bot-path"]) as String,
        botToken = System.getenv("BOT_TOKEN"),
        webhookPath = requireNotNull((properties["telegram"] as Map<*, *>)["webhook-path"]) as String,
        botName = requireNotNull((properties["telegram"] as Map<*, *>)["bot-name"]) as String,
        errorText = requireNotNull((properties["message"] as Map<*, *>)["error-text"]) as String,
        listOfCommands = listOfCommands
    )
}

data class TelegramConfig(
    val botPath: String,
    val botToken: String,
    val webhookPath: String,
    val botName: String,
    val errorText: String,
    val listOfCommands: List<BotCommand>
)

private val listOfCommands = listOf(
    BotCommand("/start", "Получить описание"),
    BotCommand("/help", "Получить описание"),
    BotCommand("/today", "Скинуть сегодняшнюю картинку"),
    BotCommand("/random", "Скинуть случайную картинку"),
    BotCommand("/schedule", "Переключить отправку сегодняшней картинки по расписанию"),
    BotCommand("/translate", "Переключить перевод EN->RU"),
)