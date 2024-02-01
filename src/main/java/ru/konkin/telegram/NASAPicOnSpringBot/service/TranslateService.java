package ru.konkin.telegram.NASAPicOnSpringBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.konkin.telegram.NASAPicOnSpringBot.client.YandexTranslateApiClient;
import ru.konkin.telegram.NASAPicOnSpringBot.model.NasaObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class TranslateService {
    @Autowired
    private YandexTranslateApiClient client;

    public NasaObject translateTitleAndExplanation(NasaObject input) throws IOException {
        String title = input.title();
        String explanation = input.explanation();
        List<String> translatedTexts = client
                .translate(new ArrayList<>(Arrays.asList(title, explanation)));
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
        return new NasaObject(input.credit(), input.copyright(), input.date(), translatedExplanation,
                input.hdUrl(), input.mediaType(), input.serviceVersion(), translatedTitle, input.url());
    }
}