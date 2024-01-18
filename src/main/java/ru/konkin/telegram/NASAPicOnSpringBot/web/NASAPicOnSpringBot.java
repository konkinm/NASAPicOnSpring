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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
            !Дата должна быть не раньше 1995-06-20!
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
                    String date = matcher.group(0);
                    if (!Objects.equals(date, "")) {
                        givePostedOnDatePicture(date);
                    } else {
                        System.err.println("Parsing error!");
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
            sendFormattedAndTranslatedPostWithDate(nasaObject, chat_id);
        } else {
            sendFormattedPostWithDate(nasaObject, chat_id);
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
            sendFormattedAndTranslatedPostWithDate(nasaObject, chat_id);
        } else {
            sendFormattedPostWithDate(nasaObject, chat_id);
        }
    }

    private void givePostedOnDatePicture(String date) throws IOException {
        NasaObject nasaObject = null;
        try {
            nasaObject = nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest("?date=" + date));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            sendMessage("Нет картинки на эту дату.", chat_id);
        }
        assert nasaObject != null;
        if (withTranslate) {
            sendFormattedAndTranslatedPostWithDate(nasaObject, chat_id);
        } else {
            sendFormattedPostWithDate(nasaObject, chat_id);
        }
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

    private void sendFormattedAndTranslatedPostWithDate(NasaObject nasaObject, long chat_id) throws IOException {
        String title = nasaObject.getTitle();
        String explanation = nasaObject.getExplanation();
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
        String hdurl = nasaObject.getHdurl();
        sendMessage("<a href=\""
                        + (hdurl.isEmpty() ? nasaObject.getUrl() : hdurl)
                        + "\" >"
                        + "<b>"
                        + translatedTitle
                        + "</b>"
                        + "</a>"
                        + "\n(Опубликовано " + nasaObject.getDate() + ")\n\n"
                        + translatedExplanation,
                chat_id);
    }

    private void sendFormattedPostWithDate(NasaObject nasaObject, long chat_id) {
        String hdurl = nasaObject.getHdurl();
        sendMessage("<a href=\""
                        + (hdurl.isEmpty() ? nasaObject.getUrl() : hdurl)
                        + "\" >"
                        + "<b>"
                        + nasaObject.getTitle()
                        + "</b>"
                        + "</a>"
                        + "\n(Posted on " + nasaObject.getDate() + ")\n\n"
                        + nasaObject.getExplanation(),
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