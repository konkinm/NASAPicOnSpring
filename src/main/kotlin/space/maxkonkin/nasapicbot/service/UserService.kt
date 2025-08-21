package space.maxkonkin.nasapicbot.service

import org.slf4j.LoggerFactory
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.repository.UserRepository

class UserService(private val repository: UserRepository) {
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
        if (getById(user.chatId) != null) {
            log.info("new user saved")
            save(user)
        }
    }

    fun update(user: User) {
        log.info("updating user with chat_id={}", user.chatId)
        repository.update(user)
    }

    fun deleteById(id: Long) {
        log.info("deleting user with chat_id={}", id)
        repository.deleteById(id)
    }
}

private val log = LoggerFactory.getLogger(UserService::class.java)
