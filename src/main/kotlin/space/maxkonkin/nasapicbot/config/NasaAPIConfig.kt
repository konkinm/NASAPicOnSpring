package space.maxkonkin.nasapicbot.config

fun loadNasaAPIConfig(properties: Map<String, Any>): NasaAPIConfig {
    return NasaAPIConfig(requireNotNull((properties["apod"] as Map<*, *>)["api-url"] as String))
}

data class NasaAPIConfig(
    val apiBaseUri: String
)