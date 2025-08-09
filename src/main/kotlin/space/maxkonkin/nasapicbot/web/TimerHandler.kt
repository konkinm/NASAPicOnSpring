package space.maxkonkin.nasapicbot.web

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import space.maxkonkin.nasapicbot.config.SpringConfig
import space.maxkonkin.nasapicbot.model.TimerMessage
import space.maxkonkin.nasapicbot.service.UserService
import java.io.IOException

fun handle(timerMessage: TimerMessage): String {
    log.debug("Initializing Spring context...")
    val ctx = AnnotationConfigApplicationContext(SpringConfig::class.java)
    log.debug("Done.")
    log.debug("Instantiating userService...")
    val userService = ctx.getBean(UserService::class.java)
    log.debug("Done.")
    log.debug("Instantiating bot...")
    val nasaPicOnSpringBot = ctx.getBean(NASAPicOnSpringBot::class.java)
    log.debug("Done.")
    val messages = timerMessage.messages
    if (messages.size > 1) throw RuntimeException("Multiple messages not supported!")
    return try {
        log.debug("Getting message payload...")
        val payload = messages.first().details?.payload
        log.debug("Payload: {}", payload)
        val users = userService.getAll()
        for (user in users) {
            if (user.isScheduled) {
                val sendMessage = nasaPicOnSpringBot.giveTodayPicture(user)
                nasaPicOnSpringBot.execute(sendMessage)
                log.info("Message sent to chat_id={}", sendMessage.chatId)
                Thread.sleep(50)
            }
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
