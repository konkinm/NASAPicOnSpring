package ru.konkin.telegram.NASAPicOnSpringBot.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.konkin.telegram.NASAPicOnSpringBot.model.UserObject;
import ru.konkin.telegram.NASAPicOnSpringBot.repo.UserRepo;
import ru.konkin.telegram.NASAPicOnSpringBot.service.NASAPicOnSpringBot;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class GiveController {
    private final NASAPicOnSpringBot nasaPicOnSpringBot;
    private UserRepo userRepo;

    @GetMapping("/give")
    public ResponseEntity<Object> handleRequest() throws IOException {
        initUserRepo();
        long chat_id = userRepo.getUsers().get(0).getCHAT_ID();
        nasaPicOnSpringBot.giveTodayPicture(chat_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void initUserRepo() {
        userRepo.addUser(new UserObject(229590625,"Max"));
    }
}