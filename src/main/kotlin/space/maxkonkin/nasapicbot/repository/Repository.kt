package space.maxkonkin.nasapicbot.repository

interface Repository<T> {
    fun getAll(): List<T>
    fun getById(id: Long): T?
    fun save(entity: T)
    fun update(entity: T)
    fun deleteById(chatId: Long)
}
