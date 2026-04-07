package space.maxkonkin.nasapicbot.service

import org.slf4j.LoggerFactory
import space.maxkonkin.nasapicbot.client.YandexCloudClient
import space.maxkonkin.nasapicbot.model.Token
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.repository.UserRepository

class UserService(
    private val repository: UserRepository,
    private val cloudClient: YandexCloudClient
) {
    fun getAll(): List<User> {
        log.info("get all users")
        return repository.getAll()
    }

    fun getById(id: Long): User? {
        log.info("get user with chat_id={}", id)
        return repository.getById(id)
    }

    fun save(user: User) {
        log.info("saving user with chat_id={}", user.chatId)
        repository.save(user)
    }

    fun saveNew(user: User) {
        log.info("saving user if not exists with chat_id={}", user.chatId)
        if (getById(user.chatId) == null) {
            log.info("new user saved")
            save(user)
        }
    }

    fun update(user: User) {
        log.info("updating user with chat_id={}", user.chatId)
        repository.update(user)
    }

    fun updateSchedule(user: User, token: Token?) {
        log.info("updating user schedule with chat_id={}", user.chatId)
        if (token != null ) {
            if (cloudClient.isTriggerCreated(user.triggerId, token.token)) {
                if (user.isScheduled) {
                    cloudClient.resumeTrigger(user.triggerId, token.token)
                } else {
                    cloudClient.pauseTrigger(user.triggerId, token.token)
                }
                repository.update(user)
            } else {
                val triggerId = cloudClient.createTrigger(user.chatId.toString(), token.token)
                if (user.isScheduled.not()) cloudClient.pauseTrigger(triggerId, token.token)
                repository.update(user.copy(triggerId = triggerId))
            }
        }
    }

    fun delete(user: User, token: Token) {
        log.info("deleting user with chat_id={}", user.chatId)
        if (user.triggerId?.isNotBlank() == true) cloudClient.deleteTrigger(user.triggerId, token.token)
        repository.deleteById(user.chatId)
    }
}

private val log = LoggerFactory.getLogger(UserService::class.java)
