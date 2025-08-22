package space.maxkonkin.nasapicbot.web

import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient
import space.maxkonkin.nasapicbot.config.bot
import space.maxkonkin.nasapicbot.model.TimerMessage
import space.maxkonkin.nasapicbot.service.UserService
import java.io.IOException

fun handle(timerMessage: TimerMessage): String {
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
    val userService = GlobalContext.get().get<UserService>()
    val messages = timerMessage.messages
    if (messages.size > 1) throw RuntimeException("Multiple messages not supported!")
    return try {
        log.debug("Getting message payload...")
        val payload = requireNotNull(messages.first().details?.payload)
        log.debug("Payload: {}", payload)
        val user = userService.getById(payload.toLong())
        if (user != null) {
            if (user.isScheduled) {
                val sendMessage = nasaPicOnSpringBot.giveTodayPicture(user)
                telegramClient.execute(sendMessage)
                log.info("Message sent to chat_id={}", sendMessage.chatId)
            } else {
                log.info("Message not sent. Scheduling is off for user with chat_id=${user.chatId}")
            }
        } else {
            log.error("Message not sent. User with chat_id=$payload not found")
        }
        "OK"
    } catch (e: Exception) {
        when (e) {
            is IOException, is TelegramApiException, is InterruptedException -> {
                log.error(e.message)
                "ERROR"
            }

            else -> throw e
        }
    }
}

private val log = LoggerFactory.getLogger("timer_handler")
