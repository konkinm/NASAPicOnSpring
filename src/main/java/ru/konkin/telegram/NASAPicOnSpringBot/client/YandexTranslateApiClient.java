package ru.konkin.telegram.NASAPicOnSpringBot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.konkin.telegram.NASAPicOnSpringBot.config.YandexTranslateApiConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YandexTranslateApiClient {

    private static final CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(30000)
                    .setRedirectsEnabled(false)
                    .build())
            .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> translate(List<String> inputTexts) throws IOException {
        final HttpPost httpPost = new HttpPost(YandexTranslateApiConfig.API_BASE_URI);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", YandexTranslateApiConfig.API_TOKEN);

        StringBuilder stringBuilder = new StringBuilder();
        for (String inputText : inputTexts) {
            stringBuilder.append("\"");
            stringBuilder.append(inputText);
            stringBuilder.append("\",");
        }

        final String json = "{\n" +
                "    \"folderId\": \"" + YandexTranslateApiConfig.FOLDER_ID + "\",\n" +
                "    \"texts\": [" + stringBuilder + "],\n" +
                "    \"targetLanguageCode\": \"ru\"\n" +
                "}";

        final StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assert response != null;

        JsonNode jsonNode = mapper.readTree(response.getEntity().getContent());
        List<JsonNode> textNodes = jsonNode.findValues("text");
        List<String> texts = new ArrayList<>();

        for (JsonNode node : textNodes) {
            texts.add(node.textValue());
        }

        return texts;
    }
}