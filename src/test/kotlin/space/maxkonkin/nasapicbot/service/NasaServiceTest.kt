package space.maxkonkin.nasapicbot.service

import io.mockk.*
import org.junit.jupiter.api.Test
import space.maxkonkin.nasapicbot.client.NasaApiClient
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.Nasa
import space.maxkonkin.nasapicbot.model.ScheduleState
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.repository.NasaRowTableRepository
import space.maxkonkin.nasapicbot.to.NasaTo
import java.io.IOException
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NasaServiceTest {

    private val nasaApiClient = mockk<NasaApiClient>()
    private val nasaRepository = mockk<NasaRowTableRepository>()
    private val translateService = mockk<TranslateService>()

    private val enUser = User(1L, "user", ScheduleState.NONE, LangCode.EN)
    private val ruUser = User(2L, "user", ScheduleState.NONE, LangCode.RU)

    private val nasaTo = NasaTo(
        credit = null,
        copyright = null,
        date = "2024-01-15",
        explanation = "Explanation",
        hdUrl = "https://example.com/hd.jpg",
        mediaType = "image",
        serviceVersion = "v1",
        title = "Title",
        url = "https://example.com/img.jpg"
    )

    private val cachedNasa = Nasa(
        langCode = LangCode.RU,
        credit = null,
        copyright = null,
        date = LocalDate.of(2024, 1, 15),
        explanation = "Cached explanation",
        hdUrl = "https://example.com/hd.jpg",
        mediaType = "image",
        serviceVersion = "v1",
        title = "Cached title",
        url = "https://example.com/img.jpg"
    )

    // --- getToday ---

    @Test
    fun `getToday returns cached result when available`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = true)
        every { nasaApiClient.makeNasaApiRequest("") } returns "https://api.example.com"
        every { nasaApiClient.getNASAObject("https://api.example.com") } returns nasaTo
        every { nasaRepository.getByDateAndLang(LocalDate.of(2024, 1, 15), LangCode.RU) } returns cachedNasa

        val result = service.getToday(ruUser)

        assertEquals("Cached title", result?.title)
        assertEquals("Cached explanation", result?.explanation)
        verify(exactly = 0) { translateService.translateTitleAndExplanation(any(), any()) }
        verify(exactly = 0) { nasaRepository.save(any()) }
    }

    @Test
    fun `getToday returns API result directly for EN user without translation`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = true)
        every { nasaApiClient.makeNasaApiRequest("") } returns "https://api.example.com"
        every { nasaApiClient.getNASAObject("https://api.example.com") } returns nasaTo
        every { nasaRepository.getByDateAndLang(LocalDate.of(2024, 1, 15), LangCode.EN) } returns null

        val result = service.getToday(enUser)

        assertEquals("Title", result?.title)
        verify(exactly = 0) { translateService.translateTitleAndExplanation(any(), any()) }
    }

    @Test
    fun `getToday translates and caches when RU user and withTranslate enabled`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = true)
        val translatedTo = nasaTo.copy(title = "Заголовок", explanation = "Объяснение")
        every { nasaApiClient.makeNasaApiRequest("") } returns "https://api.example.com"
        every { nasaApiClient.getNASAObject("https://api.example.com") } returns nasaTo
        every { nasaRepository.getByDateAndLang(LocalDate.of(2024, 1, 15), LangCode.RU) } returns null
        every { translateService.translateTitleAndExplanation(nasaTo, LangCode.RU) } returns translatedTo
        every { nasaRepository.save(any()) } just Runs

        val result = service.getToday(ruUser)

        assertEquals("Заголовок", result?.title)
        verify { translateService.translateTitleAndExplanation(nasaTo, LangCode.RU) }
        verify { nasaRepository.save(any()) }
    }

    @Test
    fun `getToday skips translation when withTranslate disabled`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = false)
        every { nasaApiClient.makeNasaApiRequest("") } returns "https://api.example.com"
        every { nasaApiClient.getNASAObject("https://api.example.com") } returns nasaTo
        every { nasaRepository.getByDateAndLang(LocalDate.of(2024, 1, 15), LangCode.RU) } returns null

        val result = service.getToday(ruUser)

        assertEquals("Title", result?.title)
        verify(exactly = 0) { translateService.translateTitleAndExplanation(any(), any()) }
        verify(exactly = 0) { nasaRepository.save(any()) }
    }

    @Test
    fun `getToday returns null on IOException`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService)
        every { nasaApiClient.makeNasaApiRequest("") } returns "https://api.example.com"
        every { nasaApiClient.getNASAObject("https://api.example.com") } throws IOException("Network error")

        val result = service.getToday(enUser)

        assertNull(result)
    }

    // --- getRandom ---

    @Test
    fun `getRandom returns API result for EN user`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = true)
        every { nasaApiClient.makeNasaApiRequest("?count=1") } returns "https://api.example.com?count=1"
        every { nasaApiClient.getNASAObjects("https://api.example.com?count=1") } returns listOf(nasaTo)
        every { nasaRepository.getByDateAndLang(LocalDate.of(2024, 1, 15), LangCode.EN) } returns null

        val result = service.getRandom(enUser)

        assertEquals("Title", result?.title)
    }

    @Test
    fun `getRandom returns cached result when available`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = true)
        every { nasaApiClient.makeNasaApiRequest("?count=1") } returns "https://api.example.com?count=1"
        every { nasaApiClient.getNASAObjects("https://api.example.com?count=1") } returns listOf(nasaTo)
        every { nasaRepository.getByDateAndLang(LocalDate.of(2024, 1, 15), LangCode.RU) } returns cachedNasa

        val result = service.getRandom(ruUser)

        assertEquals("Cached title", result?.title)
        verify(exactly = 0) { translateService.translateTitleAndExplanation(any(), any()) }
    }

    @Test
    fun `getRandom returns null on IOException`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService)
        every { nasaApiClient.makeNasaApiRequest("?count=1") } returns "https://api.example.com?count=1"
        every { nasaApiClient.getNASAObjects("https://api.example.com?count=1") } throws IOException("Network error")

        val result = service.getRandom(enUser)

        assertNull(result)
    }

    // --- getOnDate ---

    @Test
    fun `getOnDate returns API result for a specific date`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = true)
        val date = LocalDate.of(2024, 1, 15)
        every { nasaApiClient.makeNasaApiRequest("?date=2024-01-15") } returns "https://api.example.com?date=2024-01-15"
        every { nasaApiClient.getNASAObject("https://api.example.com?date=2024-01-15") } returns nasaTo
        every { nasaRepository.getByDateAndLang(date, LangCode.EN) } returns null

        val result = service.getOnDate(date, enUser)

        assertEquals("Title", result?.title)
    }

    @Test
    fun `getOnDate translates when RU user and withTranslate enabled`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService, withTranslate = true)
        val date = LocalDate.of(2024, 1, 15)
        val translatedTo = nasaTo.copy(title = "Заголовок", explanation = "Объяснение")
        every { nasaApiClient.makeNasaApiRequest("?date=2024-01-15") } returns "https://api.example.com?date=2024-01-15"
        every { nasaApiClient.getNASAObject("https://api.example.com?date=2024-01-15") } returns nasaTo
        every { nasaRepository.getByDateAndLang(date, LangCode.RU) } returns null
        every { translateService.translateTitleAndExplanation(nasaTo, LangCode.RU) } returns translatedTo
        every { nasaRepository.save(any()) } just Runs

        val result = service.getOnDate(date, ruUser)

        assertEquals("Заголовок", result?.title)
        verify { translateService.translateTitleAndExplanation(nasaTo, LangCode.RU) }
    }

    @Test
    fun `getOnDate returns null on IOException`() {
        val service = NasaService(nasaApiClient, nasaRepository, translateService)
        val date = LocalDate.of(2024, 1, 15)
        every { nasaApiClient.makeNasaApiRequest("?date=2024-01-15") } returns "https://api.example.com?date=2024-01-15"
        every { nasaApiClient.getNASAObject("https://api.example.com?date=2024-01-15") } throws IOException("Network error")

        val result = service.getOnDate(date, enUser)

        assertNull(result)
    }
}