package space.maxkonkin.nasapicbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:application-\${SPRING_PROFILE}.yaml"], factory = YamlPropertySourceFactory::class)
class NasaAPIConfig {
    @Value("\${apod.api-url}")
    lateinit var apiBaseUri: String
}
