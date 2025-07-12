package ru.gb.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    BEING_COLLECTED("Mahsulotlar yig'ilmoqda"),
    ON_THE_WAY("Yo'lda"),
    TAKE_IT_AWAY("Olib ketishingiz mumkin"),
    GIVEN_TO_CUSTOMER("Xaridorga berilgan");

    private final String value;

}
