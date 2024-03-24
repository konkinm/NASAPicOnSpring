package space.maxkonkin.nasapicbot.repository;


import space.maxkonkin.nasapicbot.model.User;
import space.maxkonkin.nasapicbot.util.ThrowingConsumer;
import tech.ydb.table.query.Params;
import tech.ydb.table.result.ResultSetReader;
import tech.ydb.table.values.PrimitiveValue;

import java.util.ArrayList;
import java.util.List;

public class UserDao implements Dao<User>{

    private final EntityManager entityManager = new EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"));

    @Override
    public List<User> findAll() {
        var users = new ArrayList<User>();
        entityManager.execute("select * from nasapic_users", Params.empty(), ThrowingConsumer.unchecked(result -> {
            ResultSetReader resultSet = result.getResultSet(0);
            while (resultSet.next()) {
                users.add(User.fromResultSet(resultSet));
            }
        }));
        return users;
    }

    @Override
    public User getById(long chatId) {
        var users = new ArrayList<User>();
        entityManager.execute("declare $chat_id as Uint64; " +
                        "select chat_id, name, is_scheduled, translate_lang_code from nasapic_users " +
                        "where chat_id = $chat_id",
                Params.of("$chat_id", PrimitiveValue.newUint64(chatId)), ThrowingConsumer.unchecked(result -> {
                    ResultSetReader resultSet = result.getResultSet(0);
                    while (resultSet.next()) {
                        users.add(User.fromResultSet(resultSet));
                    }
                }));
        return !users.isEmpty() ? users.get(0) : null;
    }

    @Override
    public void save(User user) {
        String query = "declare $chat_id as Uint64;" +
                "declare $name as Utf8;" +
                "declare $is_scheduled as Bool;" +
                "declare $translate_lang_code as Utf8;" +
                "insert into nasapic_users(chat_id, name, is_scheduled, translate_lang_code) " +
                "values ($chat_id, $name, $is_scheduled,  $translate_lang_code)";
        Params params = Params.of("$chat_id", PrimitiveValue.newInt64(user.getChatId()),
                "$name", PrimitiveValue.newText(user.getName()),
                "$is_scheduled", PrimitiveValue.newBool(user.isScheduled()),
                "$translate_lang_code", PrimitiveValue.newText(user.getTranslateLangCode()));
        entityManager.execute(query,params);
    }

    @Override
    public void deleteById(long chatId) {
        entityManager.execute(
                "declare $chat_id as Uint64;" +
                        "delete from nasapic_users where chat_id = $chat_id",
                Params.of("$uuid", PrimitiveValue.newUint64(chatId)));
    }
}
