package space.maxkonkin.nasapicbot.config

fun loadYandexTranslateConfig(properties: Map<String, Any>): YandexApiConfig {
    return YandexApiConfig(
        apiBaseUri = requireNotNull((properties["yandex-api"] as Map<*, *>)["api-url"]) as String,
        folderId = requireNotNull((properties["yandex-api"] as Map<*, *>)["folder-id"]) as String,
        apiToken = System.getenv("YA_API_TOKEN"),
        triggerApiUri = requireNotNull((properties["yandex-api"] as Map<*, *>)["trigger-api-url"]) as String,
        serviceAccountId = requireNotNull((properties["yandex-api"] as Map<*, *>)["service-account-id"]) as String,
        triggerFunctionId = requireNotNull((properties["yandex-api"] as Map<*, *>)["trigger-function-id"]) as String,
    )
}

data class YandexApiConfig(
    val apiBaseUri: String,
    val folderId: String,
    val apiToken: String,
    val triggerApiUri: String,
    val serviceAccountId: String,
    val triggerFunctionId: String,
)