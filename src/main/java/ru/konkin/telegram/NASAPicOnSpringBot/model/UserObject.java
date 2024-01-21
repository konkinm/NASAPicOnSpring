package ru.konkin.telegram.NASAPicOnSpringBot.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

public record UserObject(long CHAT_ID, String NAME) {
}
