package space.maxkonkin.nasapicbot.model

import tech.ydb.table.result.ResultSetReader

data class User(
    val chatId: Long,
    val name: String,
    val isScheduled: Boolean,
    val translateLangCode: LangCode,
    val triggerId: String? = null
) {
    companion object {
        fun fromResultSet(resultSet: ResultSetReader): User {
            val chatId = resultSet.getColumn("chat_id").uint64
            val name = resultSet.getColumn("name").text
            val isScheduled = resultSet.getColumn("is_scheduled").bool
            val translateLangCode = LangCode.valueOf(resultSet.getColumn("translate_lang_code").text.uppercase())
            val triggerIdValue = resultSet.getColumn("trigger_id").value.asOptional()
            val triggerId = if (triggerIdValue.isPresent) triggerIdValue.get().asData().text else null
            return User(chatId, name, isScheduled, translateLangCode, triggerId)
        }
    }
}
