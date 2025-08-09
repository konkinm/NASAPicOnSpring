package space.maxkonkin.nasapicbot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import space.maxkonkin.nasapicbot.client.NasaApiClient
import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.User
import space.maxkonkin.nasapicbot.repository.NasaRowTableRepository
import space.maxkonkin.nasapicbot.to.NasaTo
import space.maxkonkin.nasapicbot.util.fromTo
import space.maxkonkin.nasapicbot.util.getTo
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class NasaService(
    private val nasaApiClient: NasaApiClient,
    private val nasaRepository: NasaRowTableRepository,
    private val translateService: TranslateService,
    private val withTranslate: Boolean = false
) {
    fun getToday(user: User): NasaTo? {
        return try {
            val to = nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest(""))
            getNasaTo(user, to)
        } catch (e: IOException) {
            log.error(e.message)
            null
        }
    }

    fun getRandom(user: User): NasaTo? {
        return try {
            val to = nasaApiClient.getNASAObjects(nasaApiClient.makeNasaApiRequest("?count=1")).first()
            getNasaTo(user, to)
        } catch (e: Exception) {
            when (e) {
                is IOException, is InterruptedException -> {
                    log.error(e.message)
                    null
                }

                else -> throw e
            }
        }
    }

    fun getOnDate(date: LocalDate, user: User): NasaTo? {
        return try {
            val to = nasaApiClient.getNASAObject(
                nasaApiClient.makeNasaApiRequest(
                    "?date=" +
                            date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
            )
            getNasaTo(user, to)
        } catch (e: IOException) {
            log.error(e.message)
            null
        }
    }

    private fun getNasaTo(user: User, to: NasaTo): NasaTo {
        val cached = nasaRepository.getByDateAndLang(
            LocalDate.parse(
                requireNotNull(to.date),
                DateTimeFormatter.ISO_LOCAL_DATE
            ),
            user.translateLangCode
        )
        return if (cached != null) {
            log.info("Loaded from cache")
            getTo(cached)
        } else if (withTranslate && user.translateLangCode != LangCode.EN) {
            val translated = translateService.translateTitleAndExplanation(to, user.translateLangCode)
            nasaRepository.save(fromTo(translated, user.translateLangCode))
            log.info("Translated and saved in cache")
            translated
        } else {
            to
        }
    }
}

private val log = LoggerFactory.getLogger(NasaService::class.java)