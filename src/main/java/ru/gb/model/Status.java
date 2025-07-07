package ru.gb.model;

import lombok.Getter;

@Getter
public enum Status {

    OUT_OF_STOCK("TUGAGAN");
    private final String outOfStock;


    Status(String outOfStock) {
        this.outOfStock = outOfStock;
    }
}
