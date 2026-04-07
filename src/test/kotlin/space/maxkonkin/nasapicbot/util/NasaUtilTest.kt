package space.maxkonkin.nasapicbot.util

import org.junit.jupiter.api.Test
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.Nasa
import space.maxkonkin.nasapicbot.to.NasaTo
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NasaUtilTest {

    private val sampleTo = NasaTo(
        credit = null,
        copyright = "NASA",
        date = "2024-01-15",
        explanation = "A beautiful nebula.",
        hdUrl = "https://example.com/image_hd.jpg",
        mediaType = "image",
        serviceVersion = "v1",
        title = "Nebula",
        url = "https://example.com/image.jpg"
    )

    @Test
    fun `getFormattedMessage contains title, date and explanation`() {
        val result = getFormattedMessage(sampleTo, "Posted on")

        assertTrue(result.contains("Nebula"))
        assertTrue(result.contains("A beautiful nebula."))
        assertTrue(result.contains("2024-01-15"))
        assertTrue(result.contains("Posted on"))
    }

    @Test
    fun `getFormattedMessage does not include HD link when url differs from hdUrl`() {
        val result = getFormattedMessage(sampleTo, "Posted on")

        assertFalse(result.contains(">HD<"))
    }

    @Test
    fun `getFormattedMessage includes HD link when url equals hdUrl and media is image`() {
        val to = sampleTo.copy(url = "https://example.com/image.jpg", hdUrl = "https://example.com/image.jpg")

        val result = getFormattedMessage(to, "Posted on")

        assertTrue(result.contains(">HD<"))
    }

    @Test
    fun `getFormattedMessage does not include HD link for video even when url equals hdUrl`() {
        val to = sampleTo.copy(
            url = "https://example.com/video.mp4",
            hdUrl = "https://example.com/video.mp4",
            mediaType = "video"
        )

        val result = getFormattedMessage(to, "Posted on")

        assertFalse(result.contains(">HD<"))
    }

    @Test
    fun `getFormattedMessage wraps title in bold anchor tag`() {
        val result = getFormattedMessage(sampleTo, "Posted on")

        assertTrue(result.contains("<b>Nebula</b>"))
        assertTrue(result.contains("<a href=\"https://example.com/image.jpg\">"))
    }

    @Test
    fun `fromTo converts NasaTo to Nasa with correct fields`() {
        val nasa = fromTo(sampleTo, LangCode.RU)

        assertEquals(LangCode.RU, nasa.langCode)
        assertEquals("NASA", nasa.copyright)
        assertEquals(LocalDate.of(2024, 1, 15), nasa.date)
        assertEquals("A beautiful nebula.", nasa.explanation)
        assertEquals("Nebula", nasa.title)
        assertEquals("https://example.com/image.jpg", nasa.url)
        assertEquals("https://example.com/image_hd.jpg", nasa.hdUrl)
        assertEquals("image", nasa.mediaType)
    }

    @Test
    fun `getTo converts Nasa to NasaTo with ISO date string`() {
        val nasa = Nasa(
            langCode = LangCode.EN,
            credit = null,
            copyright = "NASA",
            date = LocalDate.of(2024, 1, 15),
            explanation = "A beautiful nebula.",
            hdUrl = "https://example.com/image_hd.jpg",
            mediaType = "image",
            serviceVersion = "v1",
            title = "Nebula",
            url = "https://example.com/image.jpg"
        )

        val to = getTo(nasa)

        assertEquals("2024-01-15", to.date)
        assertEquals("NASA", to.copyright)
        assertEquals("A beautiful nebula.", to.explanation)
        assertEquals("Nebula", to.title)
        assertEquals("https://example.com/image.jpg", to.url)
    }

    @Test
    fun `cloneWithReplacedUrl replaces url and preserves all other fields`() {
        val cloned = cloneWithReplacedUrl(sampleTo, "https://example.com/new.jpg")

        assertEquals("https://example.com/new.jpg", cloned.url)
        assertEquals(sampleTo.title, cloned.title)
        assertEquals(sampleTo.explanation, cloned.explanation)
        assertEquals(sampleTo.date, cloned.date)
        assertEquals(sampleTo.hdUrl, cloned.hdUrl)
        assertEquals(sampleTo.copyright, cloned.copyright)
        assertEquals(sampleTo.mediaType, cloned.mediaType)
    }

    @Test
    fun `fromTo then getTo is a round-trip`() {
        val nasa = fromTo(sampleTo, LangCode.EN)
        val roundTripped = getTo(nasa)

        assertEquals(sampleTo.title, roundTripped.title)
        assertEquals(sampleTo.explanation, roundTripped.explanation)
        assertEquals(sampleTo.date, roundTripped.date)
        assertEquals(sampleTo.url, roundTripped.url)
    }
}