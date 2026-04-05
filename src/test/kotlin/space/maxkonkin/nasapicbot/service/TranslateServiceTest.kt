package space.maxkonkin.nasapicbot.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import space.maxkonkin.nasapicbot.client.YandexTranslateApiClient
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.to.NasaTo
import kotlin.test.assertEquals

class TranslateServiceTest {

    private val client = mockk<YandexTranslateApiClient>()
    private val translateService = TranslateService(client)

    private val input = NasaTo(
        credit = null,
        copyright = null,
        date = "2024-01-15",
        explanation = "Original explanation",
        hdUrl = null,
        mediaType = "image",
        serviceVersion = "v1",
        title = "Original title",
        url = "https://example.com/image.jpg"
    )

    @Test
    fun `translates both title and explanation`() {
        every { client.translate(any(), LangCode.RU) } returns listOf("Заголовок", "Объяснение")

        val result = translateService.translateTitleAndExplanation(input, LangCode.RU)

        assertEquals("Заголовок", result.title)
        assertEquals("Объяснение", result.explanation)
    }

    @Test
    fun `preserves non-text fields after translation`() {
        every { client.translate(any(), LangCode.RU) } returns listOf("Заголовок", "Объяснение")

        val result = translateService.translateTitleAndExplanation(input, LangCode.RU)

        assertEquals(input.date, result.date)
        assertEquals(input.url, result.url)
        assertEquals(input.mediaType, result.mediaType)
        assertEquals(input.copyright, result.copyright)
    }

    @Test
    fun `falls back to original explanation when only one translated text returned`() {
        every { client.translate(any(), LangCode.RU) } returns listOf("Заголовок")

        val result = translateService.translateTitleAndExplanation(input, LangCode.RU)

        assertEquals("Заголовок", result.title)
        assertEquals(input.explanation, result.explanation)
    }

    @Test
    fun `falls back to original explanation when translated list is empty`() {
        every { client.translate(any(), LangCode.RU) } returns emptyList()

        val result = translateService.translateTitleAndExplanation(input, LangCode.RU)

        assertEquals("", result.title)
        assertEquals(input.explanation, result.explanation)
    }

    @Test
    fun `sends both title and explanation to client`() {
        every { client.translate(any(), LangCode.RU) } returns listOf("T", "E")

        translateService.translateTitleAndExplanation(input, LangCode.RU)

        verify {
            client.translate(
                match { it.contains("Original title") && it.contains("Original explanation") },
                LangCode.RU
            )
        }
    }

    @Test
    fun `handles null title and explanation gracefully`() {
        val inputWithNulls = input.copy(title = null, explanation = null)
        every { client.translate(any(), LangCode.RU) } returns listOf("T", "E")

        val result = translateService.translateTitleAndExplanation(inputWithNulls, LangCode.RU)

        assertEquals("T", result.title)
        assertEquals("E", result.explanation)
    }
}