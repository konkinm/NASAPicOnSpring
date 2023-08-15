package ru.konkin.telegram.NASAPicOnSpringBot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.konkin.telegram.NASAPicOnSpringBot.config.NasaAPIConfig;
import ru.konkin.telegram.NASAPicOnSpringBot.model.NasaObject;

import java.io.IOException;

public class NasaApiClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String makeNasaApiRequest(String param) {
        return NasaAPIConfig.API_BASE_URI + param;
    }

    public static NasaObject getNASAObject(String uri) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client
                     .execute(new HttpGet(uri))) {
            return mapper.readValue(response.getEntity().getContent(), NasaObject.class);
        }
    }

   public static NasaObject[] getNASAObjects(String uri) throws IOException, InterruptedException {
       try (CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client
                    .execute(new HttpGet(uri))) {
           return mapper.readValue(response.getEntity().getContent(), NasaObject[].class);
       }
   }
}