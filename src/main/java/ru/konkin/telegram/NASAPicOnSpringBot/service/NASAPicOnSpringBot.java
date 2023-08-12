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
        NasaObject nasaObject;
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
                            try {
                                nasaObject = NasaApiClient.getNASAObject(NasaApiClient.makeNasaApiRequest("?date=" + date));
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                                throw new RuntimeException(e);
                            }
                            sendFormattedPostWithDate(nasaObject);
                        } else {
                            System.out.println("Parsing error!");
                            throw new RuntimeException("Parsing error!");
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
        NasaObject nasaObject;
        try {
            nasaObject = NasaApiClient.getNASAObjects(NasaApiClient.makeNasaApiRequest("?count=1"))[0];
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        String title = nasaObject.getTitle();
        String explanation = nasaObject.getExplanation();
        List<String> translatedTexts = YandexTranslateApiClient
                .translate(new ArrayList<>(Arrays.asList(title,explanation)));
        String translatedTitle = translatedTexts.get(0);
        String translatedExplanation = translatedTexts.get(1);
        sendFormattedPostWithDateAndCustomTitleAndExplanation(nasaObject, translatedTitle, translatedExplanation);
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

    private void sendFormattedPostWithDateAndCustomTitleAndExplanation(NasaObject nasaObject,
                                                                       String customTitle,
                                                                       String customExplanation) {
        sendMessage("<a href=\""
                + nasaObject.getUrl()
                + "\" >"
                + "<b>"
                + customTitle
                + "</b>"
                + "</a>"
                + "\n(Posted on " + nasaObject.getDate() + ")\n\n"
                + customExplanation);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            handleUpdate(update);
        } catch (Exception e) {
            chat_id = update.getMessage().getChatId();
            sendMessage(this.errorText);
            System.out.println(e.getMessage());
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
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
}