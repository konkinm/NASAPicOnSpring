package space.maxkonkin.nasapicbot.web;

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
import org.telegram.telegrambots.starter.SpringWebhookBot;
import space.maxkonkin.nasapicbot.model.NasaObject;
import space.maxkonkin.nasapicbot.service.NasaService;
import space.maxkonkin.nasapicbot.service.TranslateService;
import space.maxkonkin.nasapicbot.util.NasaObjectUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    NasaService nasaService;

    @Autowired
    TranslateService translateService;

    public NASAPicOnSpringBot(SetWebhook setWebhook, String botToken) {
        super(setWebhook, botToken);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            return handleUpdate(update);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public SendMessage handleUpdate(Update update) throws IOException {
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
                            return givePostedOnDatePicture(date);
                        } else {
                            return sendMessage("Введённая дата должна быть не раньше 1995-06-20 и не позже сегодняшней даты", chat_id);
                        }
                    } catch (Exception e) {
                        System.err.println("Parsing error! " + e.getMessage());
                        return sendMessage("Неверный формат даты.\nВведите дату в формате <b>YYYY-MM-DD</b>", chat_id);
                    }
                } else {
                    switch (text) {
                        case "/start", "/help" -> {
                            return sendMessage(HELP_TEXT, chat_id);
                        }
                        case "/give" -> {
                            return giveTodayPicture(chat_id);
                        }
                        case "/random" -> {
                            return giveRandomPicture(chat_id);
                        }
                        default -> {
                            return sendMessage("Команда не поддерживается", chat_id);
                        }
                    }
                }
            }
        }
        return null;
    }

    private SendMessage giveRandomPicture(long chat_id) throws IOException {
        NasaObject random = nasaService.getRandom();
        assert random != null;
        if (withTranslate) {
            return sendTranslatedAndFormattedMessage(random, chat_id);
        } else {
            return sendFormattedMessage(random, chat_id);
        }
    }

    public SendMessage giveTodayPicture(long chat_id) throws IOException {
        NasaObject today = nasaService.getToday();
        assert today != null;
        if (withTranslate) {
            return sendTranslatedAndFormattedMessage(today, chat_id);
        } else {
            return sendFormattedMessage(today, chat_id);
        }
    }

    private SendMessage givePostedOnDatePicture(LocalDate date) throws IOException {
        NasaObject onDate = nasaService.getOnDate(date);
        assert onDate != null;
        if (withTranslate) {
            return sendTranslatedAndFormattedMessage(onDate, chat_id);
        } else {
            return sendFormattedMessage(onDate, chat_id);
        }
    }

    private SendMessage sendFormattedMessage(NasaObject nasaObject, long chat_id) {
        return sendMessage(NasaObjectUtil.getFormattedMessage(nasaObject), chat_id);
    }

    private SendMessage sendTranslatedAndFormattedMessage(NasaObject nasaObject, long chat_id) throws IOException {
        NasaObject translated = translateService.translateTitleAndExplanation(nasaObject);
        return sendMessage(NasaObjectUtil.getFormattedMessage(translated), chat_id);
    }

    private SendMessage sendMessage(String messageText, long chat_id) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText(messageText);
        message.enableHtml(true);
        return message;
    }
}
