package ru.gb.model;

import lombok.Getter;

@Getter
public enum Roles {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    Roles(String value) {
        this.value = value;
    }

}