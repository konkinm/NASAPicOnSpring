package ru.konkin.telegram.NASAPicOnSpringBot.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
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
    String botPath;
    String botUsername;
    String errorText;
    public static long chat_id;

    public static final String HELP_TEXT = """
            Привет, я бот NASA! Я высылаю ссылки на картинки (или видео) с описанием по запросу. Введи команду:
            /give чтобы получить сегодняшнюю картинку;
            /random чтобы получить случайную картинку.
            Либо введи дату в формате <b>YYYY-MM-DD</b> и я пришлю ссылку на картинку с описанием, опубликованную в тот день.
            !Дата должна быть не раньше 1995-06-20!
            Напоминаю, что картинки на сайте NASA обновляются раз в сутки""";

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
                        case "/start", "/help" -> sendMessage(HELP_TEXT);
                        case "/give" -> giveTodayPicture();
                        case "/random" -> giveRandomPicture();
                        default -> sendMessage("Я не понимаю :(");
                    }
                }
            }
        }
    }

    private void giveRandomPicture() throws IOException {
        NasaObject nasaObject = null;
        try {
            nasaObject = NasaApiClient.getNASAObjects(NasaApiClient.makeNasaApiRequest("?count=1"))[0];
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
        assert nasaObject != null;
        sendFormattedAndTranslatedPostWithDate(nasaObject);
    }


    private void giveTodayPicture() throws IOException {
        NasaObject nasaObject = null;
        try {
            nasaObject = NasaApiClient.getNASAObject(NasaApiClient.makeNasaApiRequest(""));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        assert nasaObject != null;
        sendFormattedAndTranslatedPostWithDate(nasaObject);
    }

    private void givePostedOnDatePicture(String date) throws IOException {
        NasaObject nasaObject = null;
        try {
            nasaObject = NasaApiClient.getNASAObject(NasaApiClient.makeNasaApiRequest("?date=" + date));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            sendMessage("Нет картинки на эту дату.");
        }
        assert nasaObject != null;
        sendFormattedAndTranslatedPostWithDate(nasaObject);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            handleUpdate(update);
        } catch (Exception e) {
            chat_id = update.getMessage().getChatId();
            sendMessage(this.errorText);
            System.err.println(e.getMessage());
        }
        return null;
    }

    private void sendFormattedAndTranslatedPostWithDate(NasaObject nasaObject) throws IOException {
        String title = nasaObject.getTitle();
        String explanation = nasaObject.getExplanation();
        List<String> translatedTexts = YandexTranslateApiClient
                .translate(new ArrayList<>(Arrays.asList(title,explanation)));
        String translatedTitle = "";
        if (translatedTexts.size() > 0) {
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
        sendMessage("<a href=\""
                + nasaObject.getUrl()
                + "\" >"
                + "<b>"
                + translatedTitle
                + "</b>"
                + "</a>"
                + "\n(Опубликовано " + nasaObject.getDate() + ")\n\n"
                + translatedExplanation);
    }

    private void sendMessage(String messageText) {
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