package space.maxkonkin.nasapicbot.repository;


import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.util.ThrowingConsumer;
import tech.ydb.table.query.Params;
import tech.ydb.table.result.ResultSetReader;
import tech.ydb.table.values.PrimitiveValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public class UserRepository implements Repository<User> {
    private final EntityManager entityManager = new EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"));

    @Override
    public List<User> getAll() {
        var users = new ArrayList<User>();
        entityManager.execute("SELECT * FROM nasapic_users", Params.empty(), ThrowingConsumer.unchecked(result -> {
            ResultSetReader resultSet = result.getResultSet(0);
            while (resultSet.next()) {
                users.add(User.fromResultSet(resultSet));
            }
        }));
        return users;
    }

    @Override
    public Optional<User> getById(long id) {
        var users = new ArrayList<User>();
        entityManager.execute("DECLARE $chat_id AS Uint64; " +
                        "SELECT chat_id, name, is_scheduled, translate_lang_code FROM nasapic_users " +
                        "WHERE chat_id = $chat_id",
                Params.of("$chat_id", PrimitiveValue.newUint64(id)), ThrowingConsumer.unchecked(result -> {
                    ResultSetReader resultSet = result.getResultSet(0);
                    while (resultSet.next()) {
                        users.add(User.fromResultSet(resultSet));
                    }
                }));
        return !users.isEmpty() ? Optional.of(users.getFirst()) : Optional.empty();
    }

    @Override
    public void save(User user) {
        String query = "DECLARE $chat_id AS Uint64;" +
                "DECLARE $name AS Utf8;" +
                "DECLARE $is_scheduled AS Bool;" +
                "DECLARE $translate_lang_code AS Utf8;" +
                "INSERT INTO nasapic_users (chat_id, name, is_scheduled, translate_lang_code) " +
                "VALUES ($chat_id, $name, $is_scheduled,  $translate_lang_code)";
        Params params = Params.of("$chat_id", PrimitiveValue.newUint64(user.getChatId()),
                "$name", PrimitiveValue.newText(user.getName()),
                "$is_scheduled", PrimitiveValue.newBool(user.isScheduled()),
                "$translate_lang_code", PrimitiveValue.newText(user.getTranslateLangCode().getCode()));
        entityManager.execute(query, params);
    }

    @Override
    public void update(User user) {
        String query = "DECLARE $chat_id as Uint64;" +
                "DECLARE $name as Utf8;" +
                "DECLARE $is_scheduled as Bool;" +
                "DECLARE $translate_lang_code as Utf8;" +
                "UPSERT INTO nasapic_users (chat_id, name, is_scheduled, translate_lang_code) " +
                "VALUES ($chat_id, $name, $is_scheduled,  $translate_lang_code)";
        Params params = Params.of("$chat_id", PrimitiveValue.newUint64(user.getChatId()),
                "$name", PrimitiveValue.newText(user.getName()),
                "$is_scheduled", PrimitiveValue.newBool(user.isScheduled()),
                "$translate_lang_code", PrimitiveValue.newText(user.getTranslateLangCode().getCode()));
        entityManager.execute(query,params);
    }

    @Override
    public void deleteById(long id) {
        entityManager.execute(
                "DECLARE $chat_id as Uint64;" +
                        "DELETE FROM nasapic_users WHERE chat_id = $chat_id",
                Params.of("$chat_id", PrimitiveValue.newUint64(id)));
    }
}
