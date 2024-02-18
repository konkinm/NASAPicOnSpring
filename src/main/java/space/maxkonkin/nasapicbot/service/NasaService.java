package space.maxkonkin.nasapicbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.maxkonkin.nasapicbot.client.NasaApiClient;
import space.maxkonkin.nasapicbot.model.NasaObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class NasaService {
    @Autowired
    private NasaApiClient nasaApiClient;

    public NasaObject getToday() {
        try {
            return nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest(""));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public NasaObject getRandom() {
        try {
            return nasaApiClient.getNASAObjects(nasaApiClient.makeNasaApiRequest("?count=1"))[0];
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public NasaObject getOnDate(LocalDate date) {
        try {
            return nasaApiClient.getNASAObject(nasaApiClient.makeNasaApiRequest("?date=" +
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
