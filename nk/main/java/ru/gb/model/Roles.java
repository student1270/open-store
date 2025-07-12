package ru.gb.model;

import lombok.Getter;

@Getter
public enum Roles {
    ORDER_ADMIN("ORDER_ADMIN"),
    USER("USER"),
    WAREHOUSE_ADMIN("WAREHOUSE_ADMIN");

    private final String value;

    Roles(String value) {
        this.value = value;
    }

}