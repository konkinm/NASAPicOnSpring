package space.maxkonkin.nasapicbot.repository;

import space.maxkonkin.nasapicbot.model.User;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    List<T> findAll();

    Optional<User> getById(long chatId);

    void save(T t);

    void deleteById(long chatId);

}