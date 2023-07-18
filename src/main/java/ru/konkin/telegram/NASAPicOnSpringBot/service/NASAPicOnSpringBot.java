package ru.konkin.telegram.NASAPicOnSpringBot.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.konkin.telegram.NASAPicOnSpringBot.model.NasaObject;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class NASAPicOnSpringBot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String botToken;
    String errorText;
    public static long chat_id;

    public static final String HELP_TEXT = "Привет, я бот NASA! Я высылаю ссылки на картинки по запросу. Введи команду " +
            "/give чтобы получить сегодняшнюю картинку.\n" +
            "Напоминаю, что картинки на сайте NASA обновляются раз в сутки";
    private boolean DATE_MODE = false;

    public NASAPicOnSpringBot(SetWebhook setWebhook) {
        super(setWebhook);
    }

    public void createCommands() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","Получить описание"));
        listOfCommands.add(new BotCommand("/help","Получить описание"));
        listOfCommands.add(new BotCommand("/give","Скинуть сегодняшнюю картинку"));
        listOfCommands.add(new BotCommand("/random","Скинуть случайную картинку"));
        listOfCommands.add(new BotCommand("/date","Перейти в режим ввода даты"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
                                nasaObject = Utils.getNASAObject(Utils.makeRequest("?date=" + date));
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
                        case "/start":
                        case "/help":
                            sendMessage(HELP_TEXT);
                            break;
                        case "/give":
                            giveTodayPicture();
                            break;
                        case "/random":
                            giveRandomPicture();
                            break;
                        case "/date":
                            sendMessage("Введите дату в формате YYYY-MM-DD, но не раньше 1995-06-20:");
                            DATE_MODE = true;
                            break;
                        default:
                            sendMessage("Я не понимаю :(");
                    }
                }
                log.info("handling update ID:" + update.getUpdateId());
            }
        }
    }

    private void giveRandomPicture() {
        NasaObject nasaObject;
        try {
            nasaObject = Utils.getNASAObjects(Utils.makeRequest("?count=1"))[0];
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendFormattedPostWithDate(nasaObject);
    }

    private void giveTodayPicture() {
        NasaObject nasaObject;
        try {
            nasaObject = Utils.getNASAObject(Utils.makeRequest(""));
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