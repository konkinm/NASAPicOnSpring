package space.maxkonkin.nasapicbot.config

import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException
import java.io.InputStream

object PropertiesLoader {
    fun loadProperties(): Map<String, Any> {
        val resourcePath = "/application-${System.getenv("PROFILE")}.yaml"
        val inputStream: InputStream = this::class.java.getResourceAsStream(resourcePath)
            ?: throw FileNotFoundException("Resource not found: $resourcePath")
        
        return inputStream.use { stream ->
            val yaml = Yaml()
            yaml.load(stream) ?: emptyMap()
        }
    }
}
