package space.maxkonkin.nasapicbot.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import space.maxkonkin.nasapicbot.model.UserObject;
import space.maxkonkin.nasapicbot.repo.UserRepo;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class GiveController {
    private final NASAPicOnSpringBot nasaPicOnSpringBot;
    private UserRepo userRepo;

    @GetMapping("/give")
    public ResponseEntity<?> handleRequest() throws IOException {
        initUserRepo();
        long chat_id = userRepo.getUsers().get(0).CHAT_ID();
        nasaPicOnSpringBot.giveTodayPicture(chat_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void initUserRepo() {
        userRepo.addUser(new UserObject(229590625,"Max"));
    }
}