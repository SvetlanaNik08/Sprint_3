package com.example;

import org.apache.commons.lang3.RandomStringUtils;

public class ScooterRegisterCourier {
    public static Courier getRandom() {
        String courierLogin = RandomStringUtils.randomAlphabetic(10);
        String courierPassword = RandomStringUtils.randomAlphabetic(10);
        String courierFirstName = RandomStringUtils.randomAlphabetic(10);
        return new Courier(courierLogin, courierPassword, courierFirstName);
    }
}
