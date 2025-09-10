package space.maxkonkin.nasapicbot.web

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.webhook.TelegramWebhookBot
import space.maxkonkin.nasapicbot.exception.UserNotFoundException
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.Token
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.service.MessageService
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
    private val messageService: MessageService,
    private val botPath: String,
    private val botName: String,
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

    private fun stripBotNameFromCommand(command: String): String {
        if (command.contains("@")) {
            val atIndex = command.indexOf('@')
            val botNameInCommand = command.substring(atIndex + 1)
            if (botNameInCommand.equals(botName, ignoreCase = true)) {
                return command.substring(0, atIndex)
            }
        }
        return command
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
                                messageService.getMessage("error.invalid_date_range", user.locale()),
                                chatId
                            )
                        }
                    } catch (e: Exception) {
                        System.err.println("Parsing error! " + e.message)
                        sendMessage(messageService.getMessage("error.invalid_date_format", user.locale()), chatId)
                    }
                } else {
                    val processedText = stripBotNameFromCommand(text)
                    return when (processedText) {
                        "/start", "/help" -> {
                            sendMessage(messageService.getMessage("help.text", user.locale()), chatId)
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

                        "/translate" -> {
                            toggleTranslate(user)
                        }

                        else -> {
                            sendMessage(messageService.getMessage("error.unsupported_command", user.locale()), chatId)
                        }
                    }
                }
            }
        }
        return null
    }

    private fun giveRandomPicture(user: User): SendMessage {
        val random = nasaService.getRandom(user)
        return sendFormattedMessage(user, requireNotNull(random) { "Unable to send message" }, user.chatId)
    }

    fun giveTodayPicture(user: User): SendMessage {
        val today = nasaService.getToday(user)
        return sendFormattedMessage(user, requireNotNull(today) { "Unable to send message" }, user.chatId)
    }

    private fun givePostedOnDatePicture(date: LocalDate, user: User): SendMessage {
        val onDate = nasaService.getOnDate(date, user)
        return sendFormattedMessage(user, requireNotNull(onDate) { "Unable to send message" }, user.chatId)
    }

    private fun toggleSchedule(user: User, token: Token?): SendMessage {
        val isScheduled = !user.isScheduled
        userService.updateSchedule(user.copy(isScheduled = isScheduled), token)
        return sendMessage(messageService.getScheduleMessage(isScheduled, user.locale()), user.chatId)
    }

    private fun toggleTranslate(
        user: User,
        targetLang: LangCode = LangCode.RU
    ): SendMessage {
        val updatedUser = if (user.translateLangCode != LangCode.EN) {
            user.copy(translateLangCode = LangCode.EN)
        } else {
            user.copy(translateLangCode = targetLang)
        }
        userService.update(updatedUser)
        val isEnabled = updatedUser.translateLangCode != LangCode.EN
        return sendMessage(messageService.getTranslationMessage(isEnabled, updatedUser.locale()), updatedUser.chatId)
    }

    private fun sendFormattedMessage(user: User, nasaTo: NasaTo, chatId: Long): SendMessage {
        return sendMessage(getFormattedMessage(nasaTo, messageService.getMessage("message.posted_on", user.locale())), chatId)
    }

    private fun sendMessage(messageText: String, chatId: Long): SendMessage {
        val message = SendMessage.builder()
            .parseMode("HTML")
            .chatId(chatId)
            .text(messageText)
            .build()
        return message
    }
    
    private fun User.locale(): String {
        return translateLangCode.code
    }
}