package space.maxkonkin.nasapicbot.model

import tech.ydb.table.result.ResultSetReader

data class User(
    var chatId: Long,
    var name: String,
    var isScheduled: Boolean,
    var translateLangCode: LangCode
) {
    companion object {
        fun fromResultSet(resultSet: ResultSetReader): User {
            val chatId = resultSet.getColumn("chat_id").uint64
            val name = resultSet.getColumn("name").text
            val isScheduled = resultSet.getColumn("is_scheduled").bool
            val translateLangCode = LangCode.valueOf(resultSet.getColumn("translate_lang_code").text.uppercase())
            return User(chatId, name, isScheduled, translateLangCode)
        }
    }
}
