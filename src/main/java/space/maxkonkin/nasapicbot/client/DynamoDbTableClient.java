package space.maxkonkin.nasapicbot.client;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.stereotype.Component;
import space.maxkonkin.nasapicbot.config.DynamoDbClientConfig;

@Component
public class DynamoDbTableClient {
    private final DynamoDbClientConfig config;
    public DynamoDbTableClient(DynamoDbClientConfig config) {
        this.config = config;
    }

    public Table table() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(config.getServiceEndpoint(), config.getSigningRegion()))
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        return dynamoDB.getTable(config.getTable());
    }
}
