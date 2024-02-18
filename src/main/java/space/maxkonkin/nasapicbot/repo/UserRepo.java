package space.maxkonkin.nasapicbot.repo;

import lombok.Getter;
import space.maxkonkin.nasapicbot.model.UserObject;

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
