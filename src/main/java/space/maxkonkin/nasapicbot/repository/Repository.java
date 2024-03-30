package space.maxkonkin.nasapicbot.repository;

import space.maxkonkin.nasapicbot.model.User;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    List<T> getAll();

    Optional<User> getById(long id);

    void save(T t);

    void update(T t);

    void deleteById(long chatId);

}