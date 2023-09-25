package ru.konkin.telegram.NASAPicOnSpringBot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramConfig {
    @Value("${telegram.bot-path}")
    String botPath;
    @Value("${telegram.bot-token}")
    String botToken;
    @Value("${telegram.webhook-path}")
    String webhookPath;
    @Value("${telegram.bot-name}")
    String botName;
    @Value("${message.errorText.text}")
    String errorText;
    @Value("${translate}")
    Boolean withTranslate;
}