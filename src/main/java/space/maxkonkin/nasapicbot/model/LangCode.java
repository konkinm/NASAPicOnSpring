package space.maxkonkin.nasapicbot.model;

import lombok.Getter;

@Getter
public enum LangCode {
    EN("en"),
    RU("ru");

    private final String code;

    LangCode(String code) {
        this.code = code;
    }
}
