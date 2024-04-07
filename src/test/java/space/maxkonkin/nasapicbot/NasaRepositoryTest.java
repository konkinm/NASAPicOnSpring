package space.maxkonkin.nasapicbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import space.maxkonkin.nasapicbot.config.SpringConfig;
import space.maxkonkin.nasapicbot.model.LangCode;
import space.maxkonkin.nasapicbot.model.Nasa;
import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.repository.NasaDynamoDbRepository;
import space.maxkonkin.nasapicbot.service.NasaService;
import space.maxkonkin.nasapicbot.util.NasaUtil;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
public class NasaRepositoryTest {
    public static void main(String[] args) throws IOException {
        log.debug("Initializing Spring context...");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        log.debug("Done.");
        NasaDynamoDbRepository nasaRepository = ctx.getBean(NasaDynamoDbRepository.class);
        NasaService nasaService = ctx.getBean(NasaService.class);
        Nasa nasa = NasaUtil.fromTo(nasaService.getOnDate(LocalDate.of(2020, 1,1), new User(null, null, false, LangCode.EN)), LangCode.EN);
        nasaRepository.save(nasa);
        Nasa saved = nasaRepository.getByKeys(nasa.getLangCode(), nasa.getDate()).orElseThrow();
        log.info(saved.toString());
        nasaRepository.deleteByKeys(nasa.getLangCode(), nasa.getDate());
    }
}
