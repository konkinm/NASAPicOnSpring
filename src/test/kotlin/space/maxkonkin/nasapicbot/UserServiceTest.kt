package space.maxkonkin.nasapicbot

import io.mockk.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import space.maxkonkin.nasapicbot.client.YandexCloudClient
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.ScheduleState
import space.maxkonkin.nasapicbot.model.Token
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.repository.UserRepository
import space.maxkonkin.nasapicbot.service.UserService

class UserServiceTest {

    private val repository = mockk<UserRepository>()
    private val cloudClient = mockk<YandexCloudClient>()
    private val userService = UserService(repository, cloudClient)

    private val testUser = User(123L, "testUser", ScheduleState.NONE, LangCode.EN)
    private val token = Token("test-token", 3600L, "Bearer")

    // --- saveNew ---

    @Test
    fun `saveNew saves user when not exists`() {
        every { repository.getById(123L) } returns null
        every { repository.save(testUser) } just Runs

        userService.saveNew(testUser)

        verify(exactly = 1) { repository.save(testUser) }
    }

    @Test
    fun `saveNew skips save when user already exists`() {
        every { repository.getById(123L) } returns testUser

        userService.saveNew(testUser)

        verify(exactly = 0) { repository.save(any()) }
    }

    // --- toggleSchedule ---

    @Test
    fun `toggleSchedule does nothing with cloud when token is null`() {
        val result = userService.toggleSchedule(testUser, null)

        assertEquals(ScheduleState.ACTIVE, result)
        verify(exactly = 0) { cloudClient.createTrigger(any(), any()) }
        verify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `toggleSchedule NONE to ACTIVE creates trigger`() {
        val noneUser = testUser.copy(scheduleState = ScheduleState.NONE, triggerId = null)
        every { cloudClient.createTrigger("123", "test-token") } returns "new-trigger-id"
        every { repository.update(noneUser.copy(scheduleState = ScheduleState.ACTIVE, triggerId = "new-trigger-id")) } just Runs

        val result = userService.toggleSchedule(noneUser, token)

        assertEquals(ScheduleState.ACTIVE, result)
        verify { cloudClient.createTrigger("123", "test-token") }
        verify { repository.update(noneUser.copy(scheduleState = ScheduleState.ACTIVE, triggerId = "new-trigger-id")) }
        verify(exactly = 0) { cloudClient.pauseTrigger(any(), any()) }
        verify(exactly = 0) { cloudClient.resumeTrigger(any(), any()) }
    }

    @Test
    fun `toggleSchedule ACTIVE to PAUSED pauses existing trigger`() {
        val activeUser = testUser.copy(scheduleState = ScheduleState.ACTIVE, triggerId = "trigger-1")
        every { cloudClient.pauseTrigger("trigger-1", "test-token") } returns true
        every { repository.update(activeUser.copy(scheduleState = ScheduleState.PAUSED)) } just Runs

        val result = userService.toggleSchedule(activeUser, token)

        assertEquals(ScheduleState.PAUSED, result)
        verify { cloudClient.pauseTrigger("trigger-1", "test-token") }
        verify { repository.update(activeUser.copy(scheduleState = ScheduleState.PAUSED)) }
        verify(exactly = 0) { cloudClient.resumeTrigger(any(), any()) }
    }

    @Test
    fun `toggleSchedule PAUSED to ACTIVE resumes existing trigger`() {
        val pausedUser = testUser.copy(scheduleState = ScheduleState.PAUSED, triggerId = "trigger-1")
        every { cloudClient.resumeTrigger("trigger-1", "test-token") } returns true
        every { repository.update(pausedUser.copy(scheduleState = ScheduleState.ACTIVE)) } just Runs

        val result = userService.toggleSchedule(pausedUser, token)

        assertEquals(ScheduleState.ACTIVE, result)
        verify { cloudClient.resumeTrigger("trigger-1", "test-token") }
        verify { repository.update(pausedUser.copy(scheduleState = ScheduleState.ACTIVE)) }
        verify(exactly = 0) { cloudClient.pauseTrigger(any(), any()) }
    }

    // --- delete ---

    @Test
    fun `delete removes cloud trigger and deletes user from repository`() {
        val userWithTrigger = testUser.copy(triggerId = "trigger-1")
        every { cloudClient.deleteTrigger("trigger-1", "test-token") } returns true
        every { repository.deleteById(123L) } just Runs

        userService.delete(userWithTrigger, token)

        verify { cloudClient.deleteTrigger("trigger-1", "test-token") }
        verify { repository.deleteById(123L) }
    }

    @Test
    fun `delete skips trigger deletion when triggerId is null`() {
        val userWithoutTrigger = testUser.copy(triggerId = null)
        every { repository.deleteById(123L) } just Runs

        userService.delete(userWithoutTrigger, token)

        verify(exactly = 0) { cloudClient.deleteTrigger(any(), any()) }
        verify { repository.deleteById(123L) }
    }

    @Test
    fun `delete skips trigger deletion when triggerId is blank`() {
        val userWithBlankTrigger = testUser.copy(triggerId = "   ")
        every { repository.deleteById(123L) } just Runs

        userService.delete(userWithBlankTrigger, token)

        verify(exactly = 0) { cloudClient.deleteTrigger(any(), any()) }
        verify { repository.deleteById(123L) }
    }
}