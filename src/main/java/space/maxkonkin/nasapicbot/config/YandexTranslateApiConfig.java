package space.maxkonkin.nasapicbot.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YandexTranslateApiConfig {
    @Value("${yandex-api.api-url}")
    String API_BASE_URI;
    @Value("${yandex-api.folder-id}")
    String FOLDER_ID;
    @Value("${yandex-api.api-token}")
    String API_TOKEN;
}
