package space.maxkonkin.nasapicbot

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import space.maxkonkin.nasapicbot.client.YandexCloudClient
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.repository.EntityManager
import space.maxkonkin.nasapicbot.repository.UserRepository
import space.maxkonkin.nasapicbot.service.UserService

class UserServiceTest {
    private val testId = 123456L

    /*@Test
    fun `test user service`() {
        val userService = UserService(
            UserRepository(
                tableName = "nasapic_users_test",
                entityManager = EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT")),

                ),
            cloudClient = YandexCloudClient(
                config = TODO(),
                mapper = TODO(),
                httpClient = TODO()
            )
        )
        val testUser = User(testId, "testName", false, LangCode.EN)
        userService.saveNew(testUser)
        val saved = userService.getById(testId)
        log.info(saved.toString())
        val updated = User(testId, "updatedName", true, LangCode.RU)
        userService.update(updated)
        val updatedSaved = userService.getById(testId)
        log.info(updatedSaved.toString())
        userService.deleteById(testId)
        log.info("User with id=$testId deleted.")
    }*/
}

private val log = LoggerFactory.getLogger(UserServiceTest::class.java)
