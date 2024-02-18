package space.maxkonkin.nasapicbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NasaAPIConfig {
    @Value("${apod.api-url}")
    String API_BASE_URI;
}