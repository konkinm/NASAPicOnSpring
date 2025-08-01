package space.maxkonkin.nasapicbot.web

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.starter.SpringWebhookBot
import space.maxkonkin.nasapicbot.exception.UserNotFoundException
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.service.NasaService
import space.maxkonkin.nasapicbot.service.UserService
import space.maxkonkin.nasapicbot.to.NasaTo
import space.maxkonkin.nasapicbot.util.NasaUtil
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class NASAPicOnSpringBot(
    setWebhook: SetWebhook,
    botToken: String,
    private val nasaService: NasaService,
    private val userService: UserService
) : SpringWebhookBot(setWebhook, botToken) {
    private var botPath: String? = null
    private var botUsername: String? = null
    var errorText: String? = null

    override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? {
        return try {
            handleUpdate(update)
        } catch (e: Exception) {
            System.err.println(e.message)
            null
        }
    }

    override fun getBotPath(): String? {
        return botPath
    }

    fun setBotPath(path: String) {
        botPath = path
    }

    @Throws(IOException::class)
    fun handleUpdate(update: Update): SendMessage? {
        if (!update.hasCallbackQuery()) {
            if (update.hasMessage()) {
                val message = update.message
                val chatId = message.chatId
                val tgUser = message.from
                val newUser = User(chatId, tgUser.userName, false, LangCode.EN)
                userService.saveNew(newUser)
                val user = userService.getById(chatId).orElseThrow {
                    UserNotFoundException("user with chat_id=$chatId not found")
                }
                val text = message.text
                val regex = "\\d{4}-\\d{2}-\\d{2}"
                val pattern = Pattern.compile(regex)
                assert(text != null)
                val matcher = pattern.matcher(text)
                if (matcher.find()) {
                    val fromRegex = matcher.group(0)
                    return try {
                        val date = LocalDate.parse(fromRegex, DateTimeFormatter.ISO_LOCAL_DATE)
                        if (!date.isBefore(LocalDate.of(1995, 6, 20)) &&
                            !date.isAfter(LocalDate.now())
                        ) {
                            givePostedOnDatePicture(date, user)
                        } else {
                            sendMessage("Введённая дата должна быть не раньше 1995-06-20 и не позже сегодняшней даты", chatId)
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
        return sendFormattedMessage(random!!, user.chatId)
    }

    @Throws(IOException::class)
    fun giveTodayPicture(user: User): SendMessage {
        val today = nasaService.getToday(user)
        return sendFormattedMessage(today!!, user.chatId)
    }

    private fun givePostedOnDatePicture(date: LocalDate, user: User): SendMessage {
        val onDate = nasaService.getOnDate(date, user)
        return sendFormattedMessage(onDate!!, user.chatId)
    }

    private fun sendFormattedMessage(nasaTo: NasaTo, chatId: Long): SendMessage {
        return sendMessage(NasaUtil.getFormattedMessage(nasaTo), chatId)
    }

    private fun sendMessage(messageText: String?, chatId: Long): SendMessage {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = messageText!!
        message.enableHtml(true)
        return message
    }

    override fun getBotUsername(): String? {
        return botUsername
    }

    fun setBotUsername(name: String) {
        botUsername = name
    }

    companion object {
        const val HELP_TEXT = """
            Привет, я бот NASA! Я высылаю ссылки на картинки (или видео) с описанием по запросу. Введи команду:
            /today чтобы получить сегодняшнюю картинку;
            /random чтобы получить случайную картинку.
            Либо введи дату в формате <b>YYYY-MM-DD</b> и я пришлю ссылку на картинку с описанием, опубликованную в тот день.
            Дата должна быть не раньше 1995-06-20!
            Напоминаю, что картинки на сайте NASA обновляются раз в сутки"""
    }
}