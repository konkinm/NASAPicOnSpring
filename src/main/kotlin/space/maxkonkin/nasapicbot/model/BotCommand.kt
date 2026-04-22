package space.maxkonkin.nasapicbot.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

sealed class BotCommand {
    object Start     : BotCommand()
    object Help      : BotCommand()
    object Today     : BotCommand()
    object Random    : BotCommand()
    object Schedule  : BotCommand()
    object Translate : BotCommand()
    data class OnDate(val date: LocalDate) : BotCommand()
    object InvalidDateRange  : BotCommand()
    object InvalidDateFormat : BotCommand()
    object Reply     : BotCommand()
    object Unknown   : BotCommand()

    companion object {
        private val DATE_REGEX = Regex("\\d{4}-\\d{2}-\\d{2}")
        private val APOD_START = LocalDate.of(1995, 6, 20)

        fun parse(text: String, botName: String, isReply: Boolean): BotCommand {
            val dateMatch = DATE_REGEX.find(text)
            if (dateMatch != null) {
                return try {
                    val date = LocalDate.parse(dateMatch.value, DateTimeFormatter.ISO_LOCAL_DATE)
                    when {
                        date.isBefore(APOD_START) || date.isAfter(LocalDate.now()) -> InvalidDateRange
                        else -> OnDate(date)
                    }
                } catch (_: DateTimeParseException) {
                    InvalidDateFormat
                }
            }
            val command = stripBotName(text, botName)
            return when (command) {
                "/start"     -> Start
                "/help"      -> Help
                "/today"     -> Today
                "/random"    -> Random
                "/schedule"  -> Schedule
                "/translate" -> Translate
                else         -> if (isReply) Reply else Unknown
            }
        }

        private fun stripBotName(command: String, botName: String): String {
            val atIndex = command.indexOf('@')
            if (atIndex < 0) return command
            return if (command.substring(atIndex + 1).equals(botName, ignoreCase = true))
                command.substring(0, atIndex)
            else
                command
        }
    }
}