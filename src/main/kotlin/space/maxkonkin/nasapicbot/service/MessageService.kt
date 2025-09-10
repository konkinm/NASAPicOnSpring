package space.maxkonkin.nasapicbot.service

import org.yaml.snakeyaml.Yaml
import java.io.InputStream

class MessageService {
    private val messages: Map<String, Any>

    constructor() {
        val yaml = Yaml()
        val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("messages.yaml")
            ?: throw IllegalStateException("messages.yaml not found in classpath")
        messages = yaml.load(inputStream)
    }

    fun getMessage(key: String, locale: String = "ru"): String {
        val keys = key.split(".")
        var current: Any? = messages[locale]
        
        for (k in keys) {
            if (current is Map<*, *>) {
                current = current[k]
            } else {
                return "Message not found: $key"
            }
        }
        
        return current as? String ?: "Message not found: $key"
    }
    
    fun getScheduleMessage(isScheduled: Boolean, locale: String = "ru"): String {
        val key = if (isScheduled) "schedule.updated_on" else "schedule.updated_off"
        return getMessage(key, locale)
    }
    
    fun getTranslationMessage(isEnabled: Boolean, locale: String = "ru"): String {
        val key = if (isEnabled) "translation.enabled" else "translation.disabled"
        return getMessage(key, locale)
    }
}