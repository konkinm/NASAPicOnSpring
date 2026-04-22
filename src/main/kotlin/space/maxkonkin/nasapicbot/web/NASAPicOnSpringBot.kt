package space.maxkonkin.nasapicbot.web

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.webhook.TelegramWebhookBot
import space.maxkonkin.nasapicbot.exception.UserNotFoundException
import space.maxkonkin.nasapicbot.model.BotCommand
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.ScheduleState
import space.maxkonkin.nasapicbot.model.Token
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.service.MessageService
import space.maxkonkin.nasapicbot.service.NasaService
import space.maxkonkin.nasapicbot.service.UserService
import space.maxkonkin.nasapicbot.to.NasaTo
import space.maxkonkin.nasapicbot.util.getFormattedMessage

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

    fun handleUpdate(update: Update, token: Token?): SendMessage? {
        if (update.hasCallbackQuery()) return null
        if (!update.hasMessage()) return null

        val message = update.message
        val chatId = message.chatId
        val newUser = User(chatId, message.from.userName, ScheduleState.NONE, LangCode.EN)
        userService.saveNew(newUser)
        val user = userService.getById(chatId)
            ?: throw UserNotFoundException("user with chat_id=$chatId not found")

        return when (val cmd = BotCommand.parse(checkNotNull(message.text), botName, message.isReply)) {
            is BotCommand.Start, BotCommand.Help ->
                sendMessage(messageService.getMessage("help.text", user.locale()), chatId)

            is BotCommand.Today -> giveTodayPicture(user)
            is BotCommand.Random -> giveRandomPicture(user)
            is BotCommand.Schedule -> toggleSchedule(user, token)
            is BotCommand.Translate -> toggleTranslate(user)
            is BotCommand.OnDate -> givePostedOnDatePicture(cmd.date, user)
            is BotCommand.InvalidDateRange ->
                sendMessage(messageService.getMessage("error.invalid_date_range", user.locale()), chatId)

            is BotCommand.InvalidDateFormat ->
                sendMessage(messageService.getMessage("error.invalid_date_format", user.locale()), chatId)

            is BotCommand.Unknown ->
                sendMessage(messageService.getMessage("error.unsupported_command", user.locale()), chatId)

            is BotCommand.Reply -> null
        }
    }

    private fun giveRandomPicture(user: User): SendMessage {
        val random = nasaService.getRandom(user)
        return sendFormattedMessage(user, requireNotNull(random) { "Unable to send message" }, user.chatId)
    }

    fun giveTodayPicture(user: User): SendMessage {
        val today = nasaService.getToday(user)
        return sendFormattedMessage(user, requireNotNull(today) { "Unable to send message" }, user.chatId)
    }

    private fun givePostedOnDatePicture(date: java.time.LocalDate, user: User): SendMessage {
        val onDate = nasaService.getOnDate(date, user)
        return sendFormattedMessage(user, requireNotNull(onDate) { "Unable to send message" }, user.chatId)
    }

    private fun toggleSchedule(user: User, token: Token?): SendMessage {
        val newState = userService.toggleSchedule(user, token)
        return sendMessage(messageService.getScheduleMessage(newState, user.locale()), user.chatId)
    }

    private fun toggleTranslate(user: User): SendMessage {
        val updatedUser = if (user.translateLangCode != LangCode.EN) {
            user.copy(translateLangCode = LangCode.EN)
        } else {
            user.copy(translateLangCode = LangCode.RU)
        }
        userService.update(updatedUser)
        val isEnabled = updatedUser.translateLangCode != LangCode.EN
        return sendMessage(messageService.getTranslationMessage(isEnabled, updatedUser.locale()), updatedUser.chatId)
    }

    private fun sendFormattedMessage(user: User, nasaTo: NasaTo, chatId: Long): SendMessage {
        return sendMessage(
            getFormattedMessage(nasaTo, messageService.getMessage("message.posted_on", user.locale())),
            chatId
        )
    }

    private fun sendMessage(messageText: String, chatId: Long): SendMessage {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(chatId)
            .text(messageText)
            .build()
    }

    private fun User.locale(): String = translateLangCode.code
}