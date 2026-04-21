package space.maxkonkin.nasapicbot.model

enum class ScheduleState {
    NONE,   // trigger не создан
    ACTIVE, // trigger активен, рассылка идёт
    PAUSED; // trigger на паузе

    fun toggle(): ScheduleState = when (this) {
        NONE   -> ACTIVE
        ACTIVE -> PAUSED
        PAUSED -> ACTIVE
    }
}