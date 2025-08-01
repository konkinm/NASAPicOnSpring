package space.maxkonkin.nasapicbot.repository

import java.util.Optional

interface Repository<T> {
    fun getAll(): List<T>
    fun getById(id: Long): Optional<T>
    fun save(t: T)
    fun update(t: T)
    fun deleteById(chatId: Long)
}
