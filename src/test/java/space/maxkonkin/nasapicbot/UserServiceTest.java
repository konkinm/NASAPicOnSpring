package space.maxkonkin.nasapicbot;

import lombok.extern.slf4j.Slf4j;
import space.maxkonkin.nasapicbot.model.LangCode;
import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.repository.UserRepository;
import space.maxkonkin.nasapicbot.service.UserService;

@Slf4j
public class UserServiceTest {
    private static final Long testId = 123456L;

    public static void main(String[] args) {
      final UserService userService = new UserService(new UserRepository());
      User testUser = new User(testId, "testName", false, LangCode.EN);
      userService.saveNew(testUser);
      User saved = userService.getById(testId).get();
      log.info(saved.toString());
      User updated = new User(testId, "updatedName", true, LangCode.RU);
      userService.update(updated);
      User updatedSaved = userService.getById(testId).get();
      log.info(updatedSaved.toString());
      userService.deleteById(testId);
      log.info("User with id=" + testId + " deleted.");
    }
}
