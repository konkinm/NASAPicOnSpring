package ru.konkin.telegram.NASAPicOnSpringBot.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.konkin.telegram.NASAPicOnSpringBot.client.NasaApiClient;
import ru.konkin.telegram.NASAPicOnSpringBot.model.NasaObject;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class NASAPicOnSpringBot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String errorText;
    public static long chat_id;

    public static final String HELP_TEXT = "Привет, я бот NASA! Я высылаю ссылки на картинки по запросу. Введи команду " +
            "/give чтобы получить сегодняшнюю картинку.\n" +
            "Напоминаю, что картинки на сайте NASA обновляются раз в сутки";
    private boolean DATE_MODE = false;

    public NASAPicOnSpringBot(SetWebhook setWebhook, String botToken) {
        super(setWebhook, botToken);
    }


    private void handleUpdate(Update update) {
        NasaObject nasaObject;
        if (!update.hasCallbackQuery()) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                chat_id = message.getChatId();
                String text = message.getText();
                if (DATE_MODE) {
                    final String regex = "\\d{4}-\\d{2}-\\d{2}";
                    final Pattern pattern = Pattern.compile(regex);
                    assert text != null;
                    final Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        String date = matcher.group(0);
                        if (!Objects.equals(date, "")) {
                            try {
                                nasaObject = NasaApiClient.getNASAObject(NasaApiClient.makeNasaApiRequest("?date=" + date));
                            } catch (IOException e) {
                                log.error(e.getMessage());
                                throw new RuntimeException(e);
                            }
                            sendFormattedPost(nasaObject);
                            date = "";
                            DATE_MODE = false;
                        } else {
                            log.error("Parsing error!");
                            throw new RuntimeException("Parsing error!");
                        }
                    } else {
                        sendMessage("Введена неправильная дата. Введите дату в формате YYYY-MM-DD");
                    }
                } else {
                    switch (text) {
                        case "/start", "/help" -> sendMessage(HELP_TEXT);
                        case "/give" -> giveTodayPicture();
                        case "/random" -> giveRandomPicture();
                        case "/date" -> {
                            sendMessage("Введите дату в формате YYYY-MM-DD, но не раньше 1995-06-20:");
                            DATE_MODE = true;
                        }
                        default -> sendMessage("Я не понимаю :(");
                    }
                }
                log.info("handling update ID:" + update.getUpdateId());
            }
        }
    }

    private void giveRandomPicture() {
        NasaObject nasaObject;
        try {
            nasaObject = NasaApiClient.getNASAObjects(NasaApiClient.makeNasaApiRequest("?count=1"))[0];
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendFormattedPostWithDate(nasaObject);
    }

    private void giveTodayPicture() {
        NasaObject nasaObject;
        try {
            nasaObject = NasaApiClient.getNASAObject(NasaApiClient.makeNasaApiRequest(""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendFormattedPost(nasaObject);
    }

    private void sendFormattedPost(NasaObject nasaObject) {
        sendMessage("<a href=\""
                + nasaObject.getUrl()
                + "\" >"
                + "<b>"
                + nasaObject.getTitle()
                + "</b>"
                + "</a>"
                + "\n\n"
                + nasaObject.getExplanation());
    }

    private void sendFormattedPostWithDate(NasaObject nasaObject) {
        sendMessage("<a href=\""
                + nasaObject.getUrl()
                + "\" >"
                + "<b>"
                + nasaObject.getTitle()
                + "</b>"
                + "</a>"
                + "\n(Posted on " + nasaObject.getDate() + ")\n\n"
                + nasaObject.getExplanation());
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            handleUpdate(update);
        } catch (Exception e) {
            chat_id = update.getMessage().getChatId();
            sendMessage(this.errorText);
            log.error(e.getMessage());
        }
        return null;
    }

    private void sendMessage(String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText(messageText);
        message.enableHtml(true);
        try {
            execute(message);
            log.info(String.format("Message sent: \"%s\"", messageText));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}