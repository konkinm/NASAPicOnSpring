package space.maxkonkin.nasapicbot

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.service.MessageService
import space.maxkonkin.nasapicbot.service.NasaService
import space.maxkonkin.nasapicbot.service.UserService
import space.maxkonkin.nasapicbot.to.NasaTo
import space.maxkonkin.nasapicbot.web.NASAPicOnSpringBot
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NASAPicOnSpringBotTest {

    private val nasaService = mockk<NasaService>()
    private val userService = mockk<UserService>()
    private val messageService = mockk<MessageService>()
    private val bot = NASAPicOnSpringBot(
        nasaService, userService, messageService,
        botPath = "/callback",
        botName = "TestBot"
    )

    private val domainUser = User(100L, "testUser", false, LangCode.EN)
    private val nasaTo = NasaTo(
        null, null, "2024-01-15", "Explanation",
        "https://hd.url", "image", "v1", "Title", "https://img.url"
    )

    private fun buildUpdate(text: String, isReply: Boolean = false): Update {
        val tgUser = mockk<org.telegram.telegrambots.meta.api.objects.User>()
        every { tgUser.userName } returns "testUser"

        val message = mockk<Message>()
        every { message.chatId } returns 100L
        every { message.from } returns tgUser
        every { message.text } returns text
        every { message.isReply } returns isReply

        val update = mockk<Update>()
        every { update.hasCallbackQuery() } returns false
        every { update.hasMessage() } returns true
        every { update.message } returns message

        return update
    }

    @BeforeEach
    fun setUp() {
        every { userService.saveNew(any()) } just Runs
        every { userService.getById(100L) } returns domainUser
        every { messageService.getMessage("message.posted_on", "en") } returns "Posted on"
    }

    // --- routing guard ---

    @Test
    fun `returns null for callback query updates`() {
        val update = mockk<Update>()
        every { update.hasCallbackQuery() } returns true

        val result = bot.handleUpdate(update, null)

        assertNull(result)
    }

    @Test
    fun `returns null when update has no message`() {
        val update = mockk<Update>()
        every { update.hasCallbackQuery() } returns false
        every { update.hasMessage() } returns false

        val result = bot.handleUpdate(update, null)

        assertNull(result)
    }

    // --- commands ---

    @Test
    fun `start command returns help message`() {
        every { messageService.getMessage("help.text", "en") } returns "Help text"

        val result = bot.handleUpdate(buildUpdate("/start"), null)

        assertNotNull(result)
        assertEquals("Help text", result.text)
        assertEquals("100", result.chatId)
    }

    @Test
    fun `help command returns help message`() {
        every { messageService.getMessage("help.text", "en") } returns "Help text"

        val result = bot.handleUpdate(buildUpdate("/help"), null)

        assertNotNull(result)
        assertEquals("Help text", result.text)
    }

    @Test
    fun `today command calls NasaService and returns formatted message`() {
        every { nasaService.getToday(domainUser) } returns nasaTo

        val result = bot.handleUpdate(buildUpdate("/today"), null)

        assertNotNull(result)
        assertEquals("100", result.chatId)
        verify { nasaService.getToday(domainUser) }
    }

    @Test
    fun `random command calls NasaService and returns formatted message`() {
        every { nasaService.getRandom(domainUser) } returns nasaTo

        val result = bot.handleUpdate(buildUpdate("/random"), null)

        assertNotNull(result)
        verify { nasaService.getRandom(domainUser) }
    }

    @Test
    fun `schedule command toggles isScheduled from false to true and returns message`() {
        every { userService.updateSchedule(any(), null) } just Runs
        every { messageService.getScheduleMessage(true, "en") } returns "Schedule: on"

        val result = bot.handleUpdate(buildUpdate("/schedule"), null)

        assertNotNull(result)
        assertEquals("Schedule: on", result.text)
        verify { userService.updateSchedule(domainUser.copy(isScheduled = true), null) }
    }

    @Test
    fun `translate command switches EN user to RU and returns enabled message`() {
        every { userService.update(any()) } just Runs
        every { messageService.getTranslationMessage(true, "ru") } returns "Translation enabled"

        val result = bot.handleUpdate(buildUpdate("/translate"), null)

        assertNotNull(result)
        assertEquals("Translation enabled", result.text)
        verify { userService.update(domainUser.copy(translateLangCode = LangCode.RU)) }
    }

    @Test
    fun `translate command switches RU user back to EN and returns disabled message`() {
        val ruUser = domainUser.copy(translateLangCode = LangCode.RU)
        every { userService.getById(100L) } returns ruUser
        every { userService.update(any()) } just Runs
        every { messageService.getTranslationMessage(false, "en") } returns "Translation disabled"

        val result = bot.handleUpdate(buildUpdate("/translate"), null)

        assertNotNull(result)
        assertEquals("Translation disabled", result.text)
        verify { userService.update(ruUser.copy(translateLangCode = LangCode.EN)) }
    }

    @Test
    fun `command with bot name suffix is stripped and handled correctly`() {
        every { messageService.getMessage("help.text", "en") } returns "Help text"

        val result = bot.handleUpdate(buildUpdate("/start@TestBot"), null)

        assertNotNull(result)
        assertEquals("Help text", result.text)
    }

    // --- date input ---

    @Test
    fun `valid date in range dispatches to NasaService getOnDate`() {
        every { nasaService.getOnDate(any(), domainUser) } returns nasaTo

        val result = bot.handleUpdate(buildUpdate("2024-01-15"), null)

        assertNotNull(result)
        verify { nasaService.getOnDate(any(), domainUser) }
    }

    @Test
    fun `date before 1995-06-20 returns invalid range error`() {
        every { messageService.getMessage("error.invalid_date_range", "en") } returns "Date out of range"

        val result = bot.handleUpdate(buildUpdate("1995-01-01"), null)

        assertNotNull(result)
        assertEquals("Date out of range", result.text)
    }

    @Test
    fun `date in the future returns invalid range error`() {
        every { messageService.getMessage("error.invalid_date_range", "en") } returns "Date out of range"

        val result = bot.handleUpdate(buildUpdate("2099-12-31"), null)

        assertNotNull(result)
        assertEquals("Date out of range", result.text)
    }

    @Test
    fun `impossible calendar date matching regex returns format error`() {
        every { messageService.getMessage("error.invalid_date_format", "en") } returns "Invalid date format"

        // Matches \d{4}-\d{2}-\d{2} but month 99 is not a valid calendar month
        val result = bot.handleUpdate(buildUpdate("2024-99-99"), null)

        assertNotNull(result)
        assertEquals("Invalid date format", result.text)
    }

    // --- unknown / reply ---

    @Test
    fun `unknown command returns unsupported command error`() {
        every { messageService.getMessage("error.unsupported_command", "en") } returns "Command not supported"

        val result = bot.handleUpdate(buildUpdate("/unknown"), null)

        assertNotNull(result)
        assertEquals("Command not supported", result.text)
    }

    @Test
    fun `unknown command sent as a reply returns null`() {
        val result = bot.handleUpdate(buildUpdate("/unknown", isReply = true), null)

        assertNull(result)
    }
}