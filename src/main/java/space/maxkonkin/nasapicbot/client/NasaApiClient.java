package space.maxkonkin.nasapicbot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import space.maxkonkin.nasapicbot.config.NasaAPIConfig;
import space.maxkonkin.nasapicbot.to.NasaTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static space.maxkonkin.nasapicbot.util.NasaUtil.cloneWithReplacedUrl;

@Component
public class NasaApiClient {
    private final NasaAPIConfig nasaAPIConfig;
    private final ObjectMapper mapper = new ObjectMapper();

    public NasaApiClient(NasaAPIConfig nasaAPIConfig) {
        this.nasaAPIConfig = nasaAPIConfig;
    }

    public String makeNasaApiRequest(String param) {
        return nasaAPIConfig.getAPI_BASE_URI() + param;
    }

    public NasaTo getNASAObject(String uri) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client
                     .execute(new HttpGet(uri))) {
            NasaTo input = mapper.readValue(response.getEntity().getContent(), NasaTo.class);
            if (input.mediaType().equals("video") && input.url().contains("embed/")) {
                String filtered = input.url().replace("embed/", "watch?v=")
                        .replace("?rel=0", "");
                return cloneWithReplacedUrl(input, filtered);
            } else return input;
        }
    }

    public List<NasaTo> getNASAObjects(String uri) throws IOException, InterruptedException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client
                     .execute(new HttpGet(uri))) {
            List<NasaTo> tos = mapper.readValue(response.getEntity().getContent(),
                    mapper.getTypeFactory().constructCollectionType(List.class, NasaTo.class));
            return getFiltered(tos);
        }
    }

    private static List<NasaTo> getFiltered(List<NasaTo> tos) {
        List<NasaTo> filtered = new ArrayList<>();
        for (NasaTo to : tos) {
            if (to.mediaType().equals("video") && to.url().contains("embed/")) {
                String replaced = to.url().replace("embed/", "watch?v=")
                        .replace("?rel=0", "");
                filtered.add(cloneWithReplacedUrl(to, replaced));
            } else filtered.add(to);
        }
        return filtered;
    }
}