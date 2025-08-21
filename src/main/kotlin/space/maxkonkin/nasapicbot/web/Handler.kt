package space.maxkonkin.nasapicbot.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient
import space.maxkonkin.nasapicbot.config.bot
import space.maxkonkin.nasapicbot.model.QueueMessage
import java.io.IOException

fun handle(message: QueueMessage): String {
    log.debug("Initializing koin GlobalContext...")
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(bot)
        }
    }
    log.debug("Done.")
    log.debug("Instantiating bot...")
    val nasaPicOnSpringBot = GlobalContext.get().get<NASAPicOnSpringBot>()
    log.debug("Instantiating telegram client...")
    val telegramClient = GlobalContext.get().get<TelegramClient>()
    log.debug("Done.")
    val mapper = GlobalContext.get().get<ObjectMapper>()
    val messages = message.messages
    if (messages.size > 1) throw RuntimeException("Multiple messages not supported!")
    return try {
        log.debug("Getting message body...")
        val update = mapper.readValue(messages.first().details?.message?.body, Update::class.java)
        log.debug("Update: ${mapper.writeValueAsString(update)}")
        val sendMessage = nasaPicOnSpringBot.handleUpdate(update)
        telegramClient.execute(sendMessage)
        log.info("Message sent to chat_id=${sendMessage?.chatId}")
        "OK"
    } catch (e: Exception) {
        when (e) {
            is IOException, is TelegramApiException -> {
                log.error(e.message)
                "ERROR"
            }

            else -> throw e
        }
    }
}

private val log = LoggerFactory.getLogger("handler")
