package ru.konkin.telegram.NASAPicOnSpringBot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.konkin.telegram.NASAPicOnSpringBot.config.NasaAPIConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static final CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(30000)
                    .setRedirectsEnabled(false)
                    .build())
            .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String makeRequest(String param) {
        return NasaAPIConfig.API_BASE_URI + param;
    }

    public static NasaObject getNASAObject(String uri) throws IOException {
        CloseableHttpResponse response = httpClient.execute(new HttpGet(uri));
        return mapper.readValue(response.getEntity().getContent(), NasaObject.class);
    }

   public static NasaObject[] getNASAObjects(String uri) throws IOException, InterruptedException {
        CloseableHttpResponse response = httpClient.execute(new HttpGet(uri));
       TimeUnit.MILLISECONDS.sleep(100);
      return mapper.readValue(response.getEntity().getContent(), NasaObject[].class);
    }
}