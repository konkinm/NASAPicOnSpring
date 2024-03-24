package space.maxkonkin.nasapicbot.service;

import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.repository.UserDao;

import java.util.List;

public class UserService {
    private final UserDao repository;

    public UserService(UserDao repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void save(User user) {
        repository.save(user);
    }

    public void deleteBy(long chatId) {
        repository.deleteById(chatId);
    }
}
