package space.maxkonkin.nasapicbot.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import space.maxkonkin.nasapicbot.client.NasaApiClient;
import space.maxkonkin.nasapicbot.model.LangCode;
import space.maxkonkin.nasapicbot.model.Nasa;
import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.repository.NasaRepository;
import space.maxkonkin.nasapicbot.to.NasaTo;
import space.maxkonkin.nasapicbot.util.NasaUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
public class NasaService {
    @Setter
    Boolean withTranslate;

    private final NasaApiClient nasaApiClient;
    private final NasaRepository nasaRepository;
    private final TranslateService translateService;

    public NasaService(NasaApiClient nasaApiClient, NasaRepository nasaRepository, TranslateService translateService) {
        this.nasaApiClient = nasaApiClient;
        this.nasaRepository = nasaRepository;
        this.translateService = translateService;
    }

    public NasaTo getToday(User user) {
        try {
            NasaTo to = nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest(""));
            Optional<Nasa> cached = nasaRepository.getByKeys(user.getTranslateLangCode(), to.date());
            if (cached.isPresent()) {
                log.info("Loaded from cache");
                return NasaUtil.getTo(cached.get());
            } else if (withTranslate && user.getTranslateLangCode() != LangCode.EN) {
                NasaTo translated = translateService.translateTitleAndExplanation(to, user.getTranslateLangCode());
                nasaRepository.save(NasaUtil.fromTo(translated, user.getTranslateLangCode()));
                log.info("Translated and saved in cache");
                return translated;
            } else {
                return to;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public NasaTo getRandom(User user) {
        try {
            NasaTo to = nasaApiClient.getNASAObjects(nasaApiClient.makeNasaApiRequest("?count=1"))[0];
            Optional<Nasa> cached = nasaRepository.getByKeys(user.getTranslateLangCode(), to.date());
            if (cached.isPresent()) {
                log.info("Loaded from cache");
                return NasaUtil.getTo(cached.get());
            } else if (withTranslate && user.getTranslateLangCode() != LangCode.EN) {
                NasaTo translated = translateService.translateTitleAndExplanation(to, user.getTranslateLangCode());
                nasaRepository.save(NasaUtil.fromTo(translated, user.getTranslateLangCode()));
                log.info("Translated and saved in cache");
                return translated;
            } else {
                return to;
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public NasaTo getOnDate(LocalDate date, User user) {
        try {
            NasaTo to = nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest("?date=" +
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE)));
            Optional<Nasa> cached = nasaRepository.getByKeys(user.getTranslateLangCode(), to.date());
            if (cached.isPresent()) {
                log.info("Loaded from cache");
                return NasaUtil.getTo(cached.get());
            } else if (withTranslate && user.getTranslateLangCode() != LangCode.EN) {
                NasaTo translated = translateService.translateTitleAndExplanation(to, user.getTranslateLangCode());
                nasaRepository.save(NasaUtil.fromTo(translated, user.getTranslateLangCode()));
                log.info("Translated and saved in cache");
                return translated;
            } else {
                return to;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
