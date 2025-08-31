package space.maxkonkin.nasapicbot.web

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.webhook.TelegramWebhookBot
import space.maxkonkin.nasapicbot.exception.UserNotFoundException
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.Token
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.service.NasaService
import space.maxkonkin.nasapicbot.service.UserService
import space.maxkonkin.nasapicbot.to.NasaTo
import space.maxkonkin.nasapicbot.util.getFormattedMessage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class NASAPicOnSpringBot(
    private val nasaService: NasaService,
    private val userService: UserService,
    private val errorText: String,
    private val botPath: String,
    private val setWebhook: Runnable? = null,
    private val deleteWebhook: Runnable? = null
) : TelegramWebhookBot {

    override fun runDeleteWebhook() {
        deleteWebhook?.run()
    }

    override fun runSetWebhook() {
        setWebhook?.run()
    }

    override fun consumeUpdate(update: Update): BotApiMethod<*>? {
        return handleUpdate(update, null)
    }

    override fun getBotPath(): String? {
        return botPath
    }

    fun handleUpdate(update: Update, token: Token?): SendMessage? {
        if (update.hasCallbackQuery().not()) {
            if (update.hasMessage()) {
                val message = update.message
                val chatId = message.chatId
                val tgUser = message.from
                val newUser = User(chatId, tgUser.userName, false, LangCode.EN)
                userService.saveNew(newUser)
                val user =
                    userService.getById(chatId) ?: throw UserNotFoundException("user with chat_id=$chatId not found")
                val text = message.text
                val regex = "\\d{4}-\\d{2}-\\d{2}"
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(checkNotNull(text))
                if (matcher.find()) {
                    val fromRegex = matcher.group(0)
                    return try {
                        val date = LocalDate.parse(fromRegex, DateTimeFormatter.ISO_LOCAL_DATE)
                        if (!date.isBefore(LocalDate.of(1995, 6, 20)) &&
                            !date.isAfter(LocalDate.now())
                        ) {
                            givePostedOnDatePicture(date, user)
                        } else {
                            sendMessage(
                                "Введённая дата должна быть не раньше 1995-06-20 и не позже сегодняшней даты",
                                chatId
                            )
                        }
                    } catch (e: Exception) {
                        System.err.println("Parsing error! " + e.message)
                        sendMessage("Неверный формат даты.\nВведите дату в формате <b>YYYY-MM-DD</b>", chatId)
                    }
                } else {
                    return when (text) {
                        "/start", "/help" -> {
                            sendMessage(HELP_TEXT, chatId)
                        }

                        "/today" -> {
                            giveTodayPicture(user)
                        }

                        "/random" -> {
                            giveRandomPicture(user)
                        }

                        "/schedule" -> {
                            toggleSchedule(user, token)
                        }

                        else -> {
                            sendMessage(errorText, chatId)
                        }
                    }
                }
            }
        }
        return null
    }

    private fun giveRandomPicture(user: User): SendMessage {
        val random = nasaService.getRandom(user)
        return sendFormattedMessage(requireNotNull(random) { "Unable to send message" }, user.chatId)
    }

    fun giveTodayPicture(user: User): SendMessage {
        val today = nasaService.getToday(user)
        return sendFormattedMessage(requireNotNull(today) { "Unable to send message" }, user.chatId)
    }

    private fun givePostedOnDatePicture(date: LocalDate, user: User): SendMessage {
        val onDate = nasaService.getOnDate(date, user)
        return sendFormattedMessage(requireNotNull(onDate) { "Unable to send message" }, user.chatId)
    }

    private fun toggleSchedule(user: User, token: Token?): SendMessage {
        val isScheduled = !user.isScheduled
        userService.updateSchedule(user.copy(isScheduled = isScheduled), token)
        return sendMessage("Schedule was updated: ${if (isScheduled) "on" else "off"}", user.chatId)
    }

    private fun sendFormattedMessage(nasaTo: NasaTo, chatId: Long): SendMessage {
        return sendMessage(getFormattedMessage(nasaTo), chatId)
    }

    private fun sendMessage(messageText: String, chatId: Long): SendMessage {
        val message = SendMessage.builder()
            .parseMode("HTML")
            .chatId(chatId)
            .text(messageText)
            .build()
        return message
    }
}

const val HELP_TEXT = """
            Привет, я бот NASA! Я высылаю ссылки на картинки (или видео) с описанием по запросу. Введи команду:
            /today чтобы получить сегодняшнюю картинку;
            /random чтобы получить случайную картинку.
            Либо введи дату в формате <b>YYYY-MM-DD</b> и я пришлю ссылку на картинку с описанием, опубликованную в тот день.
            Дата должна быть не раньше 1995-06-20!
            Напоминаю, что картинки на сайте NASA обновляются раз в сутки.
            """