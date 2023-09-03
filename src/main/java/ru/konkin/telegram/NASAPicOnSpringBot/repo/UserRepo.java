package ru.konkin.telegram.NASAPicOnSpringBot.repo;

import lombok.Getter;
import ru.konkin.telegram.NASAPicOnSpringBot.model.UserObject;

import java.util.List;

@Getter
public class UserRepo {
    private List<UserObject> users;

    public UserRepo(List<UserObject> users) {
        this.users = users;
    }

    public void addUser(UserObject user){
        users.add(user);
    }
    
}
