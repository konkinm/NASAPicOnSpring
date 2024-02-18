package ru.konkin.telegram.NASAPicOnSpringBot.web;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.konkin.telegram.NASAPicOnSpringBot.config.SpringConfig;

import java.io.IOException;
import java.util.function.Function;

public class Handler implements Function<Update, SendMessage> {
    @Override
    public SendMessage apply(Update update) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        NASAPicOnSpringBot nasaPicOnSpringBot = ctx.getBean(NASAPicOnSpringBot.class);
        try {
            return nasaPicOnSpringBot.handleUpdate(update);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
