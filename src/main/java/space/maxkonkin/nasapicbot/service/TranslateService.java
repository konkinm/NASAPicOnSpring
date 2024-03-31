package space.maxkonkin.nasapicbot.service;

import org.springframework.stereotype.Service;
import space.maxkonkin.nasapicbot.client.YandexTranslateApiClient;
import space.maxkonkin.nasapicbot.model.LangCode;
import space.maxkonkin.nasapicbot.to.NasaTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class TranslateService {
    private final YandexTranslateApiClient client;

    public TranslateService(YandexTranslateApiClient client) {
        this.client = client;
    }

    public NasaTo translateTitleAndExplanation(NasaTo input, LangCode langCode) throws IOException {
        String title = input.title();
        String explanation = input.explanation();
        List<String> translatedTexts = client
                .translate(new ArrayList<>(Arrays.asList(title, explanation)), langCode);
        String translatedTitle = "";
        String translatedExplanation = "";
        if (!translatedTexts.isEmpty()) {
            translatedTitle = translatedTexts.get(0);
            if (translatedTexts.size() > 1) {
                translatedExplanation = translatedTexts.get(1);
            } else {
                System.out.println("WARN: 'transletedTexts' has only one element!");
                translatedExplanation = input.explanation(); // no translation
            }
        } else {
            System.err.println("'transletedTexts' is empty!");
        }
        return new NasaTo(input.credit(), input.copyright(), input.date(), translatedExplanation,
                input.hdUrl(), input.mediaType(), input.serviceVersion(), translatedTitle, input.url());
    }
}