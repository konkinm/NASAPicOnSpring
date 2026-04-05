package space.maxkonkin.nasapicbot

import io.mockk.*
import org.junit.jupiter.api.Test
import space.maxkonkin.nasapicbot.client.YandexCloudClient
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.Token
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.repository.UserRepository
import space.maxkonkin.nasapicbot.service.UserService

class UserServiceTest {

    private val repository = mockk<UserRepository>()
    private val cloudClient = mockk<YandexCloudClient>()
    private val userService = UserService(repository, cloudClient)

    private val testUser = User(123L, "testUser", false, LangCode.EN)
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

    // --- updateSchedule ---

    @Test
    fun `updateSchedule does nothing when token is null`() {
        userService.updateSchedule(testUser, null)

        verify(exactly = 0) { cloudClient.isTriggerCreated(any(), any()) }
        verify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `updateSchedule resumes existing trigger when isScheduled is true`() {
        val scheduledUser = testUser.copy(isScheduled = true, triggerId = "trigger-1")
        every { cloudClient.isTriggerCreated("trigger-1", "test-token") } returns true
        every { cloudClient.resumeTrigger("trigger-1", "test-token") } returns true
        every { repository.update(scheduledUser) } just Runs

        userService.updateSchedule(scheduledUser, token)

        verify { cloudClient.resumeTrigger("trigger-1", "test-token") }
        verify { repository.update(scheduledUser) }
        verify(exactly = 0) { cloudClient.pauseTrigger(any(), any()) }
    }

    @Test
    fun `updateSchedule pauses existing trigger when isScheduled is false`() {
        val unscheduledUser = testUser.copy(isScheduled = false, triggerId = "trigger-1")
        every { cloudClient.isTriggerCreated("trigger-1", "test-token") } returns true
        every { cloudClient.pauseTrigger("trigger-1", "test-token") } returns true
        every { repository.update(unscheduledUser) } just Runs

        userService.updateSchedule(unscheduledUser, token)

        verify { cloudClient.pauseTrigger("trigger-1", "test-token") }
        verify { repository.update(unscheduledUser) }
        verify(exactly = 0) { cloudClient.resumeTrigger(any(), any()) }
    }

    @Test
    fun `updateSchedule creates trigger without pausing when isScheduled is true`() {
        val scheduledUser = testUser.copy(isScheduled = true, triggerId = null)
        every { cloudClient.isTriggerCreated(null, "test-token") } returns false
        every { cloudClient.createTrigger("123", "test-token") } returns "new-trigger-id"
        every { repository.update(scheduledUser.copy(triggerId = "new-trigger-id")) } just Runs

        userService.updateSchedule(scheduledUser, token)

        verify { cloudClient.createTrigger("123", "test-token") }
        verify { repository.update(scheduledUser.copy(triggerId = "new-trigger-id")) }
        verify(exactly = 0) { cloudClient.pauseTrigger(any(), any()) }
    }

    @Test
    fun `updateSchedule creates and immediately pauses trigger when isScheduled is false`() {
        val unscheduledUser = testUser.copy(isScheduled = false, triggerId = null)
        every { cloudClient.isTriggerCreated(null, "test-token") } returns false
        every { cloudClient.createTrigger("123", "test-token") } returns "new-trigger-id"
        every { cloudClient.pauseTrigger("new-trigger-id", "test-token") } returns true
        every { repository.update(unscheduledUser.copy(triggerId = "new-trigger-id")) } just Runs

        userService.updateSchedule(unscheduledUser, token)

        verify { cloudClient.createTrigger("123", "test-token") }
        verify { cloudClient.pauseTrigger("new-trigger-id", "test-token") }
        verify { repository.update(unscheduledUser.copy(triggerId = "new-trigger-id")) }
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