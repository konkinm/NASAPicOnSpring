package space.maxkonkin.nasapicbot.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import space.maxkonkin.nasapicbot.config.SpringConfig;
import space.maxkonkin.nasapicbot.model.TimerMessage;
import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class TimerHandler implements Function<TimerMessage, String> {

    @Override
    public String apply(TimerMessage timerMessage) {
        log.debug("Initializing Spring context...");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        log.debug("Done.");

        log.debug("Instantiating userService...");
        final UserService userService = ctx.getBean(UserService.class);
        log.debug("Done.");

        log.debug("Instantiating bot...");
        final NASAPicOnSpringBot nasaPicOnSpringBot = ctx.getBean(NASAPicOnSpringBot.class);
        log.debug("Done.");

        final List<TimerMessage.Message> messages = timerMessage.getMessages();
        if (messages.size() > 1) throw new RuntimeException("Multiple messages not supported!");
        try {
            log.debug("Getting message payload...");
            final String payload = messages.getFirst().getDetails().getPayload();
            log.debug("Payload: " + payload);
            List<User> users = userService.getAll();
            for (User user : users) {
                if (user.isScheduled()) {
                    final SendMessage sendMessage = nasaPicOnSpringBot.giveTodayPicture(user);
                    nasaPicOnSpringBot.execute(sendMessage);
                    log.info("Message sent to chat_id=" + sendMessage.getChatId());
                    Thread.sleep(50);
                }
            }
            return "OK";
        } catch (IOException | TelegramApiException | InterruptedException e) {
            log.error(e.getMessage());
            return "ERROR";
        }
    }
}
