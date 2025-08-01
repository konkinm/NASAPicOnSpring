package space.maxkonkin.nasapicbot.service

import org.springframework.stereotype.Service
import space.maxkonkin.nasapicbot.client.YandexTranslateApiClient
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.to.NasaTo
import java.io.IOException

@Service
class TranslateService(private val client: YandexTranslateApiClient) {
    @Throws(IOException::class)
    fun translateTitleAndExplanation(input: NasaTo, langCode: LangCode): NasaTo {
        val title = input.title
        val explanation = input.explanation
        val translatedTexts = client
            .translate(arrayListOf(title!!, explanation!!), langCode)
        var translatedTitle = ""
        var translatedExplanation: String?
        if (translatedTexts.isNotEmpty()) {
            translatedTitle = translatedTexts[0]
            translatedExplanation = if (translatedTexts.size > 1) {
                translatedTexts[1]
            } else {
                println("WARN: 'translatedTexts' has only one element!")
                input.explanation // no translation
            }
        } else {
            System.err.println("'translatedTexts' is empty!")
            translatedExplanation = input.explanation
        }
        return NasaTo(
            input.credit, input.copyright, input.date, translatedExplanation,
            input.hdUrl, input.mediaType, input.serviceVersion, translatedTitle, input.url
        )
    }
}
