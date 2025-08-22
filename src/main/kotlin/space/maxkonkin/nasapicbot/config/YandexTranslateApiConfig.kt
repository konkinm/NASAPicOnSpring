package space.maxkonkin.nasapicbot.config

fun loadYandexTranslateConfig(properties: Map<String, Any>): YandexApiConfig {
    return YandexApiConfig(
        apiBaseUri = requireNotNull((properties["yandex-api"] as Map<*, *>)["api-url"]) as String,
        folderId = requireNotNull((properties["yandex-api"] as Map<*, *>)["folder-id"]) as String,
        apiToken = System.getenv("YA_API_TOKEN")
    )
}

data class YandexApiConfig(
    val apiBaseUri: String,
    val folderId: String,
    val apiToken: String
)