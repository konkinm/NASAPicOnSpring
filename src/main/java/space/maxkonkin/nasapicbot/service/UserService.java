package space.maxkonkin.nasapicbot.service;

import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.repository.UserDao;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao repository;
    public UserService(UserDao repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> getById(long id) {
        return repository.getById(id);
    }

    public void save(User user) {
        repository.save(user);
    }

    public void deleteBy(long chatId) {
        repository.deleteById(chatId);
    }
}
