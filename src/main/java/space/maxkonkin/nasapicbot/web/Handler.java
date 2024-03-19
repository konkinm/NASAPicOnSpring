package space.maxkonkin.nasapicbot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import space.maxkonkin.nasapicbot.config.SpringConfig;
import space.maxkonkin.nasapicbot.model.QueueMessage;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class Handler implements Function<QueueMessage, String> {
    @Override
    public String apply(QueueMessage message) {
        final ObjectMapper mapper = new ObjectMapper();
        log.debug("Initializing Spring context...");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        log.debug("Done.");
        log.debug("Instantiating bot...");
        final NASAPicOnSpringBot nasaPicOnSpringBot = ctx.getBean(NASAPicOnSpringBot.class);
        log.debug("Done.");
        final List<QueueMessage.Message> messages = message.getMessages();
        if (messages.size() > 1) throw new RuntimeException("Multiple messages not supported!");
        try {
            log.debug("Getting message body...");
            final Update update = mapper.readValue(messages.getFirst().getDetails().getMessage().getBody(), Update.class);
            log.debug("Update: " + mapper.writeValueAsString(update));
            final SendMessage sendMessage = nasaPicOnSpringBot.handleUpdate(update);
            nasaPicOnSpringBot.execute(sendMessage);
            log.info("Message sent to chat_id=" + sendMessage.getChatId());
            return "OK";
        } catch (IOException | TelegramApiException e) {
            log.error(e.getMessage());
            return "ERROR";
        }
    }
}
