package ru.gb.model;

import lombok.Getter;
/* This class stores user roles. Currently,
we have ADMIN and USER. Later, we will add the CREATOR role.
*/
@Getter
public enum Roles {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    Roles(String value) {
        this.value = value;
    }

}