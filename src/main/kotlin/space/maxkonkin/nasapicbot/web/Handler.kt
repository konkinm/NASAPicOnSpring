package space.maxkonkin.nasapicbot.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import space.maxkonkin.nasapicbot.config.SpringConfig
import space.maxkonkin.nasapicbot.model.QueueMessage
import java.io.IOException

fun handle(message: QueueMessage): String {
        val mapper = ObjectMapper()
        log.debug("Initializing Spring context...")
        val ctx = AnnotationConfigApplicationContext(SpringConfig::class.java)
        log.debug("Done.")
        log.debug("Instantiating bot...")
        val nasaPicOnSpringBot = ctx.getBean(NASAPicOnSpringBot::class.java)
        log.debug("Done.")
        val messages = message.messages
        if (messages.size > 1) throw RuntimeException("Multiple messages not supported!")
        return try {
            log.debug("Getting message body...")
            val update = mapper.readValue(messages.first().details?.message?.body, Update::class.java)
            log.debug("Update: {}", mapper.writeValueAsString(update))
            val sendMessage = nasaPicOnSpringBot.handleUpdate(update)
            nasaPicOnSpringBot.execute(sendMessage)
            log.info("Message sent to chat_id={}", sendMessage?.chatId)
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
