package space.maxkonkin.nasapicbot.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    List<T> getAll();

    Optional<T> getById(long id);

    void save(T t);

    void update(T t);

    void deleteById(long chatId);

}