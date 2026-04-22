package space.maxkonkin.nasapicbot.model

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BotCommandTest {

    private fun parse(text: String, isReply: Boolean = false) =
        BotCommand.parse(text, botName = "NasaBot", isReply = isReply)

    @Test
    fun `slash commands are parsed correctly`() {
        assertIs<BotCommand.Start>(parse("/start"))
        assertIs<BotCommand.Help>(parse("/help"))
        assertIs<BotCommand.Today>(parse("/today"))
        assertIs<BotCommand.Random>(parse("/random"))
        assertIs<BotCommand.Schedule>(parse("/schedule"))
        assertIs<BotCommand.Translate>(parse("/translate"))
    }

    @Test
    fun `bot name suffix is stripped`() {
        assertIs<BotCommand.Start>(parse("/start@NasaBot"))
        assertIs<BotCommand.Help>(parse("/help@NasaBot"))
    }

    @Test
    fun `unknown command returns Unknown`() {
        assertIs<BotCommand.Unknown>(parse("/foo"))
    }

    @Test
    fun `reply with unknown command returns Reply`() {
        assertIs<BotCommand.Reply>(parse("/unknown", isReply = true))
    }

    @Test
    fun `valid date in APOD range returns OnDate`() {
        val cmd = parse("2024-06-15")
        assertIs<BotCommand.OnDate>(cmd)
        assertEquals(LocalDate.of(2024, 6, 15), cmd.date)
    }

    @Test
    fun `date before APOD launch returns InvalidDateRange`() {
        assertIs<BotCommand.InvalidDateRange>(parse("1995-01-01"))
    }

    @Test
    fun `future date returns InvalidDateRange`() {
        assertIs<BotCommand.InvalidDateRange>(parse("2099-12-31"))
    }

    @Test
    fun `impossible calendar date returns InvalidDateFormat`() {
        assertIs<BotCommand.InvalidDateFormat>(parse("2024-99-99"))
    }

    @Test
    fun `APOD first day is valid`() {
        val cmd = parse("1995-06-20")
        assertIs<BotCommand.OnDate>(cmd)
        assertEquals(LocalDate.of(1995, 6, 20), cmd.date)
    }
}