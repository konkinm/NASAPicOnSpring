package space.maxkonkin.nasapicbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAll() {
        log.info("get all users");
        return repository.getAll();
    }

    public Optional<User> getById(long id) {
        log.info("get user with chat_id={}", id);
        return repository.getById(id);
    }

    public void save(User user) {
        log.info("saving user with chat_id={}", user.getChatId());
        repository.save(user);
    }

    public void saveNew(User user) {
        log.info("saving user if not exists with chat_id={}", user.getChatId());
        if (getById(user.getChatId()).isEmpty()) {
            log.info("new user saved");
            save(user);
        }
    }

    public void update(User user) {
        log.info("updating user with chat_id={}", user.getChatId());
        repository.update(user);
    }

    public void deleteById(long id) {
        log.info("deleting user with chat_id={}", id);
        repository.deleteById(id);
    }
}
