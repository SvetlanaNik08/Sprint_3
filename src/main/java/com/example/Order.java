package com.example;

import java.util.List;

public class Order {
    private final String firstName = "Иван";
    private final String lastName = "Иванов";
    private final String address = "Рябиновая ул., 1, 2";
    private final int metroStation = 4;
    private final String phone = "+7 800 111 11 11";
    private final int rentTime = 5;
    private final String deliveryDate = "2022-04-01";
    private final String comment = "очень ждем";
    private List<String> color;

    public Order() {
    }
    public Order(List<String> color) {

        this.color = color;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
    }

}
