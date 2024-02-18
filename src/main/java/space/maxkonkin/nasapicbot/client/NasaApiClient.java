package space.maxkonkin.nasapicbot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import space.maxkonkin.nasapicbot.config.NasaAPIConfig;
import space.maxkonkin.nasapicbot.model.NasaObject;

import java.io.IOException;

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

    public NasaObject getNASAObject(String uri) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client
                     .execute(new HttpGet(uri))) {
            return mapper.readValue(response.getEntity().getContent(), NasaObject.class);
        }
    }

   public NasaObject[] getNASAObjects(String uri) throws IOException, InterruptedException {
       try (CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client
                    .execute(new HttpGet(uri))) {
           return mapper.readValue(response.getEntity().getContent(), NasaObject[].class);
       }
   }
}