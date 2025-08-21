package space.maxkonkin.nasapicbot.config

import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException

object PropertiesLoader {
    fun loadProperties(): Map<String, Any> {
        val resourcePath = "/application-${System.getenv("SPRING_PROFILE")}.yaml"
        this::class.java.getResourceAsStream(resourcePath)?.use { inputStream ->
            val yaml = Yaml()
            return yaml.load(inputStream) ?: emptyMap()
        } ?: throw FileNotFoundException("Resource not found: $resourcePath")
    }
}
