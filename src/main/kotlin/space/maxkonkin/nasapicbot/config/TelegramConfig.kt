package space.maxkonkin.nasapicbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:application-\${SPRING_PROFILE}.yaml"], factory = YamlPropertySourceFactory::class)
class TelegramConfig {
    @Value("\${telegram.bot-path}")
    lateinit var botPath: String

    @Value("\${telegram.bot-token}")
    lateinit var botToken: String

    @Value("\${telegram.webhook-path}")
    lateinit var webhookPath: String

    @Value("\${telegram.bot-name}")
    lateinit var botName: String

    @Value("\${message.errorText.text}")
    lateinit var errorText: String
}
