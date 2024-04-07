package space.maxkonkin.nasapicbot.repository;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import space.maxkonkin.nasapicbot.client.DynamoDbTableClient;
import space.maxkonkin.nasapicbot.model.LangCode;
import space.maxkonkin.nasapicbot.model.Nasa;
import space.maxkonkin.nasapicbot.util.NasaUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class NasaDynamoDbRepository {
    private final DynamoDbTableClient client;

    public NasaDynamoDbRepository(DynamoDbTableClient client) {
        this.client = client;
    }

    public List<Nasa> getAll() {
        return null; //TODO: not implemented
    }

    public Optional<Nasa> getByKeys(LangCode langCode, String date) throws IOException {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("lang", langCode.getCode(), "date", date);
        Item item = client.table().getItem(spec);
        if (item != null) {
            return Optional.of(NasaUtil.fromItem(item));
        } else {
            return Optional.empty();
        }
    }

    public void save(Nasa nasa) {
        try {
            log.debug("Creating document...");
            String credit = nasa.getCredit();
            String copyright = nasa.getCopyright();
            String hdUrl = nasa.getHdUrl();
            PutItemOutcome outcome = client.table().putItem(new Item().withPrimaryKey(
                            "lang", nasa.getLangCode().getCode(),
                            "date", nasa.getDate())
                    .withString("credit", credit != null ? credit : "")
                    .withString("copyright", copyright != null ? copyright : "")
                    .withString("explanation", nasa.getExplanation())
                    .withString("media_type", nasa.getMediaType())
                    .withString("service_version", nasa.getServiceVersion())
                    .withString("hd_url", hdUrl != null ? hdUrl : "")
                    .withString("title", nasa.getTitle())
                    .withString("url", nasa.getUrl()));
            log.debug("Document created: " + outcome.getPutItemResult());
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Unable to create document.");
        }
    }

    public void deleteByKeys(LangCode langCode, String date) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey("lang", langCode.getCode(),
                "date", date);
        try {
            log.debug("Deleting document...");
            client.table().deleteItem(deleteItemSpec);
            log.debug("Document deleted.");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Unable to delete document.");
        }
    }
}
