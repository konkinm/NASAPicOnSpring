package space.maxkonkin.nasapicbot.config

fun loadYandexCloudConfig(properties: Map<String, Any>): YandexCloudConfig {
    return YandexCloudConfig(
        apiBaseUri = requireNotNull((properties["yandex-api"] as Map<*, *>)["api-url"]) as String,
        folderId = requireNotNull((properties["yandex-api"] as Map<*, *>)["folder-id"]) as String,
        apiToken = System.getenv("YA_API_TOKEN"),
        triggerApiUri = requireNotNull((properties["yandex-api"] as Map<*, *>)["trigger-api-url"]) as String,
        serviceAccountId = requireNotNull((properties["yandex-api"] as Map<*, *>)["service-account-id"]) as String,
        triggerFunctionId = requireNotNull((properties["yandex-api"] as Map<*, *>)["trigger-function-id"]) as String,
        ydbEndpoint = requireNotNull((properties["ydb"] as Map<*, *>)["endpoint"]) as String,
        ydbDatabase = System.getenv("DATABASE"),
        ydbUserTable = requireNotNull((properties["ydb"] as Map<*, *>)["user-table"]) as String,
        ydbNasaTable = requireNotNull((properties["ydb"] as Map<*, *>)["nasa-table"]) as String,
    )
}

data class YandexCloudConfig(
    val apiBaseUri: String,
    val folderId: String,
    val apiToken: String,
    val triggerApiUri: String,
    val serviceAccountId: String,
    val triggerFunctionId: String,
    val ydbEndpoint: String,
    val ydbDatabase: String,
    val ydbUserTable: String,
    val ydbNasaTable: String,
)