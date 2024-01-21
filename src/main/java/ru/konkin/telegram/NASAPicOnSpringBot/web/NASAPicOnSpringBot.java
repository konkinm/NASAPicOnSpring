package ru.konkin.telegram.NASAPicOnSpringBot.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.konkin.telegram.NASAPicOnSpringBot.client.NasaApiClient;
import ru.konkin.telegram.NASAPicOnSpringBot.client.YandexTranslateApiClient;
import ru.konkin.telegram.NASAPicOnSpringBot.model.NasaObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NASAPicOnSpringBot extends SpringWebhookBot {
    public static final String HELP_TEXT = """
            Привет, я бот NASA! Я высылаю ссылки на картинки (или видео) с описанием по запросу. Введи команду:
            /give чтобы получить сегодняшнюю картинку;
            /random чтобы получить случайную картинку.
            Либо введи дату в формате <b>YYYY-MM-DD</b> и я пришлю ссылку на картинку с описанием, опубликованную в тот день.
            Дата должна быть не раньше 1995-06-20!
            Напоминаю, что картинки на сайте NASA обновляются раз в сутки""";
    public static long chat_id;
    String botPath;
    String botUsername;
    String errorText;
    Boolean withTranslate;

    @Autowired
    private NasaApiClient nasaApiClient;

    public NASAPicOnSpringBot(SetWebhook setWebhook, String botToken) {
        super(setWebhook, botToken);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            handleUpdate(update);
        } catch (Exception e) {
            chat_id = update.getMessage().getChatId();
            System.err.println(e.getMessage());
        }
        return null;
    }

    private void handleUpdate(Update update) throws IOException {
        if (!update.hasCallbackQuery()) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                chat_id = message.getChatId();
                String text = message.getText();
                final String regex = "\\d{4}-\\d{2}-\\d{2}";
                final Pattern pattern = Pattern.compile(regex);
                assert text != null;
                final Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    String fromRegex = matcher.group(0);
                    try {
                        LocalDate date = LocalDate.parse(fromRegex, DateTimeFormatter.ISO_LOCAL_DATE);
                        if (!date.isBefore(LocalDate.of(1995, 6, 20)) &&
                                !date.isAfter(LocalDate.now())) {
                            givePostedOnDatePicture(date);
                        } else {
                            sendMessage("Введённая дата должна быть не раньше 1995-06-20 и не позже сегодняшней даты", chat_id);
                        }
                    } catch (Exception e) {
                        System.err.println("Parsing error! " + e.getMessage());
                        sendMessage("Неверный формат даты.\nВведите дату в формате <b>YYYY-MM-DD</b>", chat_id);
                    }
                } else {
                    switch (text) {
                        case "/start", "/help" -> sendMessage(HELP_TEXT, chat_id);
                        case "/give" -> giveTodayPicture(chat_id);
                        case "/random" -> giveRandomPicture(chat_id);
                        default -> sendMessage("Команда не поддерживается", chat_id);
                    }
                }
            }
        }
    }

    private void giveRandomPicture(long chat_id) throws IOException {
        NasaObject nasaObject = null;
        try {
            nasaObject = nasaApiClient.getNASAObjects(nasaApiClient.makeNasaApiRequest("?count=1"))[0];
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
        assert nasaObject != null;
        if (withTranslate) {
            sendFormattedAndTranslatedPost(nasaObject, chat_id);
        } else {
            sendFormattedPost(nasaObject, chat_id);
        }
    }

    public void giveTodayPicture(long chat_id) throws IOException {
        NasaObject nasaObject = null;
        try {
            nasaObject = nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest(""));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        assert nasaObject != null;
        if (withTranslate) {
            sendFormattedAndTranslatedPost(nasaObject, chat_id);
        } else {
            sendFormattedPost(nasaObject, chat_id);
        }
    }

    private void givePostedOnDatePicture(LocalDate date) throws IOException {
        NasaObject nasaObject = null;
        try {
            nasaObject = nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest("?date=" +
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            sendMessage("Нет картинки на эту дату.", chat_id);
        }
        assert nasaObject != null;
        if (withTranslate) {
            sendFormattedAndTranslatedPost(nasaObject, chat_id);
        } else {
            sendFormattedPost(nasaObject, chat_id);
        }
    }

    private String getFormattedMessage(NasaObject nasaObject) {
        return getFormattedMessage(nasaObject, nasaObject.title(), nasaObject.explanation());
    }

    private String getFormattedMessage(NasaObject nasaObject,
                                       String customTitle,
                                       String customExplanation) {
        String url = nasaObject.url();
        String hdUrl = nasaObject.hdUrl();
        StringBuilder message = new StringBuilder();
        message.append("<a href=\"")
                .append(url)
                .append("\">")
                .append("<b>")
                .append(customTitle)
                .append("</b>")
                .append("</a>");
        if (!url.equalsIgnoreCase(hdUrl)) {
            message.append(" | <a href=\"")
                    .append(hdUrl)
                    .append("\">")
                    .append("HD")
                    .append("</a>");
        }
        message.append("\n(Posted on ")
                .append(nasaObject.date())
                .append(")\n\n")
                .append(customExplanation);
        return message.toString();
    }

    private void sendFormattedPost(NasaObject nasaObject, long chat_id) {
        sendMessage(getFormattedMessage(nasaObject), chat_id);
    }

    private void sendFormattedAndTranslatedPost(NasaObject nasaObject, long chat_id) throws IOException {
        String title = nasaObject.title();
        String explanation = nasaObject.explanation();
        List<String> translatedTexts = YandexTranslateApiClient
                .translate(new ArrayList<>(Arrays.asList(title, explanation)));
        String translatedTitle = "";
        if (!translatedTexts.isEmpty()) {
            translatedTitle = translatedTexts.get(0);
        } else {
            System.err.println("'transletedTexts' is empty!");
        }
        String translatedExplanation = "";
        if (translatedTexts.size() > 1) {
            translatedExplanation = translatedTexts.get(1);
        } else {
            System.out.println("WARN: 'transletedTexts' has only one element!");
        }
        sendMessage(getFormattedMessage(nasaObject, translatedTitle, translatedExplanation),
                chat_id);
    }

    private void sendMessage(String messageText, long chat_id) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText(messageText);
        message.enableHtml(true);
        try {
            execute(message);
            System.out.printf("Message to chat_id %s sent successfully.\n", chat_id);
        } catch (TelegramApiException e) {
            System.err.println(e.getMessage());
        }
    }
}