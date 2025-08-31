package space.maxkonkin.nasapicbot.to

data class Trigger(
    val folderId: String,
    val name: String,
    val rule: Rule
)

data class Rule(
    val timer: Timer
)

data class Timer(
    val cronExpression: String = "0 12 ? * * *",
    val payload: String,
    val invokeFunction: InvokeFunction
)

data class InvokeFunction(
    val functionId: String,
    val functionTag: String? = null,
    val serviceAccountId: String
)