package space.maxkonkin.nasapicbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@PropertySource(value = "classpath:application-${SPRING_PROFILE}.yaml", factory = YamlPropertySourceFactory.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DynamoDbClientConfig {
    @Value("${ddb.service-endpoint}")
    String serviceEndpoint;
    @Value("${ddb.signing-region}")
    String signingRegion;
    @Value("${ddb.table}")
    String table;
}
