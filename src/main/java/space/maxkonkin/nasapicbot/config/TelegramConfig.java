package space.maxkonkin.nasapicbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Objects;
import java.util.Properties;

@Configuration
@PropertySource(value = "classpath:application-${SPRING_PROFILE}.yaml", factory = TelegramConfig.YamlPropertySourceFactory.class)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramConfig {
    @Value("${telegram.bot-path}")
    String botPath;
    @Value("${telegram.bot-token}")
    String botToken;
    @Value("${telegram.webhook-path}")
    String webhookPath;
    @Value("${telegram.bot-name}")
    String botName;
    @Value("${message.errorText.text}")
    String errorText;
    @Value("${translate}")
    Boolean withTranslate;

    protected static class YamlPropertySourceFactory implements PropertySourceFactory {

        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(encodedResource.getResource());
            Properties properties = factory.getObject();
            assert properties != null;
            return new PropertiesPropertySource(Objects.requireNonNull(
                    encodedResource.getResource().getFilename()), properties);
        }
    }
}
