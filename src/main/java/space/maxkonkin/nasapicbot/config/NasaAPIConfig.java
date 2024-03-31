package space.maxkonkin.nasapicbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = "classpath:application-${SPRING_PROFILE}.yaml", factory = YamlPropertySourceFactory.class)
public class NasaAPIConfig {
    @Value("${apod.api-url}")
    String API_BASE_URI;
}
