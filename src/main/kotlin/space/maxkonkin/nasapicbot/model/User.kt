package space.maxkonkin.nasapicbot.model

import tech.ydb.table.result.ResultSetReader

data class User(
    val chatId: Long,
    val name: String,
    val scheduleState: ScheduleState,
    val translateLangCode: LangCode,
    val triggerId: String? = null
) {
    companion object {
        fun fromResultSet(resultSet: ResultSetReader): User {
            val chatId = resultSet.getColumn("chat_id").uint64
            val name = resultSet.getColumn("name").text
            val scheduleStateValue = resultSet.getColumn("schedule_state").value.asOptional()
            val scheduleState = if (scheduleStateValue.isPresent)
                ScheduleState.valueOf(scheduleStateValue.get().asData().text.uppercase())
            else
                ScheduleState.NONE
            val translateLangCode = LangCode.valueOf(resultSet.getColumn("translate_lang_code").text.uppercase())
            val triggerIdValue = resultSet.getColumn("trigger_id").value.asOptional()
            val triggerId = if (triggerIdValue.isPresent) triggerIdValue.get().asData().text else null
            return User(chatId, name, scheduleState, translateLangCode, triggerId)
        }
    }
}
