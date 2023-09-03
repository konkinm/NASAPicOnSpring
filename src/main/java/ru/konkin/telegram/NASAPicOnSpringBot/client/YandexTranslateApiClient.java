package ru.konkin.telegram.NASAPicOnSpringBot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.konkin.telegram.NASAPicOnSpringBot.config.YandexTranslateApiConfig;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class YandexTranslateApiClient {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> translate(List<String> inputTexts) throws IOException {
        final HttpPost httpPost = new HttpPost(YandexTranslateApiConfig.API_BASE_URI);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", YandexTranslateApiConfig.API_TOKEN);

        final StringEntity entity = getStringEntity(inputTexts);
        httpPost.setEntity(entity);
        List<JsonNode> textNodes;
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(httpPost)) {
            JsonNode jsonNode = mapper.readTree(response.getEntity().getContent());
            textNodes = jsonNode.findValues("text");
        }

        return textNodes.stream().map(JsonNode::textValue).toList();
    }

    private static StringEntity getStringEntity(List<String> inputTexts) throws UnsupportedEncodingException {
        StringBuilder commaSeparatedTexts = new StringBuilder();
        for (String inputText : inputTexts) {
            String filteredInputText = inputText.replace("\"","\\\"");
            commaSeparatedTexts.append("\"");
            commaSeparatedTexts.append(filteredInputText);
            commaSeparatedTexts.append("\",");
        }

        final String json = "{\n" +
                "    \"folderId\": \"" + YandexTranslateApiConfig.FOLDER_ID + "\",\n" +
                "    \"texts\": [" + commaSeparatedTexts + "],\n" +
                "    \"targetLanguageCode\": \"ru\"\n" +
                "}";

        return new StringEntity(json);
    }
}