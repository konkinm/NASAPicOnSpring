package space.maxkonkin.nasapicbot.repository

import space.maxkonkin.nasapicbot.config.YandexCloudConfig
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.util.ThrowingConsumer
import tech.ydb.table.query.DataQueryResult
import tech.ydb.table.query.Params
import tech.ydb.table.values.PrimitiveValue

class UserRepository(
    private val config: YandexCloudConfig,
    private val entityManager: EntityManager
) : Repository<User> {

    override fun getAll(): List<User> {
        val users = ArrayList<User>()
        entityManager.execute("SELECT * FROM ${config.ydbUserTable}", Params.empty(),
            ThrowingConsumer.unchecked<DataQueryResult, RuntimeException> { result ->
                val resultSet = result.getResultSet(0)
                while (resultSet.next()) {
                    users.add(User.fromResultSet(resultSet))
                }
            })
        return users
    }

    override fun getById(id: Long): User? {
        val users = ArrayList<User>()
        entityManager.execute("DECLARE \$chat_id AS Uint64; " +
                "SELECT chat_id, name, schedule_state, translate_lang_code, trigger_id FROM ${config.ydbUserTable} " +
                "WHERE chat_id = \$chat_id",
            Params.of("\$chat_id", PrimitiveValue.newUint64(id)),
            ThrowingConsumer.unchecked<DataQueryResult, RuntimeException> { result ->
                val resultSet = result.getResultSet(0)
                while (resultSet.next()) {
                    users.add(User.fromResultSet(resultSet))
                }
            })
        return if (users.isNotEmpty()) users.first() else null
    }

    override fun save(entity: User) {
        val query = "DECLARE \$chat_id AS Uint64;" +
                "DECLARE \$name AS Utf8;" +
                "DECLARE \$schedule_state AS Utf8;" +
                "DECLARE \$translate_lang_code AS Utf8;" +
                "INSERT INTO ${config.ydbUserTable} (chat_id, name, schedule_state, translate_lang_code) " +
                "VALUES (\$chat_id, \$name, \$schedule_state, \$translate_lang_code)"
        val params = Params.of(
            "\$chat_id", PrimitiveValue.newUint64(entity.chatId),
            "\$name", PrimitiveValue.newText(entity.name),
            "\$schedule_state", PrimitiveValue.newText(entity.scheduleState.name),
            "\$translate_lang_code", PrimitiveValue.newText(entity.translateLangCode.code)
        )
        entityManager.execute(query, params)
    }

    override fun update(entity: User) {
        val query = "DECLARE \$chat_id AS Uint64;" +
                "DECLARE \$name AS Utf8;" +
                "DECLARE \$schedule_state AS Utf8;" +
                "DECLARE \$translate_lang_code AS Utf8;" +
                "DECLARE \$trigger_id AS Utf8;" +
                "UPSERT INTO ${config.ydbUserTable} (chat_id, name, schedule_state, translate_lang_code, trigger_id) " +
                "VALUES (\$chat_id, \$name, \$schedule_state, \$translate_lang_code, \$trigger_id)"
        val params = Params.of(
            "\$chat_id", PrimitiveValue.newUint64(entity.chatId),
            "\$name", PrimitiveValue.newText(entity.name),
            "\$schedule_state", PrimitiveValue.newText(entity.scheduleState.name),
            "\$translate_lang_code", PrimitiveValue.newText(entity.translateLangCode.code),
            "\$trigger_id", PrimitiveValue.newText(entity.triggerId)
        )
        entityManager.execute(query, params)
    }

    override fun deleteById(chatId: Long) {
        entityManager.execute(
            "DECLARE \$chat_id as Uint64;" +
                    "DELETE FROM ${config.ydbUserTable} WHERE chat_id = \$chat_id",
            Params.of("\$chat_id", PrimitiveValue.newUint64(chatId))
        )
    }
}
