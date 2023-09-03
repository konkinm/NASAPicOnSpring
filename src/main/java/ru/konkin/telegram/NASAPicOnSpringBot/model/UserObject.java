package ru.konkin.telegram.NASAPicOnSpringBot.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserObject {
    private final long CHAT_ID;
    private final String NAME;
}
