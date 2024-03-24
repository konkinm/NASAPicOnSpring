package space.maxkonkin.nasapicbot.repository;

import space.maxkonkin.nasapicbot.model.User;

import java.util.List;

public interface Dao<T> {

    List<T> findAll();

    User getById(long chatId);

    void save(T t);

    void deleteById(long chatId);

}