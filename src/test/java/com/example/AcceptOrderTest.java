package com.example;

import io.restassured.response.ValidatableResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AcceptOrderTest {
    private CourierClient courierClient;
    private Courier courier;
    private String courierId;
    private int statusCode;
    private Order order;
    private Integer track;
    private String orderId;


    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = ScooterRegisterCourier.getRandom();
        courierClient.create(courier);
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.extract().path("id").toString();
        order = new Order(List.of(""));
        ValidatableResponse orderResponse = courierClient.createOrder(order);
        track = orderResponse.extract().path("track");
        ValidatableResponse getOrderIdResponse = courierClient.getOrderByTrack(track);
        orderId = getOrderIdResponse.extract().path("order.id").toString();
    }

    @After
    public void tearDown() {
        courierClient.delete(courierId);
        courierClient.cancelOrder(track.toString());
    }

    @Test
    @DisplayName("Courier accepts an order (valid orderId and courierId)")
    public void testOrderCanBeAccepted() {
        ValidatableResponse acceptResponse = courierClient.acceptOrder(orderId, courierId);
        statusCode = acceptResponse.extract().statusCode();
        boolean actual = acceptResponse.extract().path("ok");

        assertThat("Заказ нельзя принять", statusCode, equalTo(SC_OK));
        assertThat("Запрос не возвращает true", actual, is(true));
    }

    @Test
    @DisplayName("Order cannot be accepted without courierId")
    public void testOrderCannotBeAcceptedWithoutCourierId() {
        ValidatableResponse acceptResponse = courierClient.acceptOrder(orderId, null);
        statusCode = acceptResponse.extract().statusCode();
        String actual = acceptResponse.extract().path("message");

        assertThat("Заказ можно принять без id курьера", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Нет сообщения 'Недостаточно данных для поиска'", actual, equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Order cannot be accepted with incorrect courierId")
    public void testOrderCannotBeAcceptedWithIncorrectCourierId() {
        ValidatableResponse acceptResponse = courierClient.acceptOrder(orderId, "0");
        statusCode = acceptResponse.extract().statusCode();
        String actual = acceptResponse.extract().path("message");

        assertThat("Заказ можно принять с неверным id курьера", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Нет сообщения 'Курьера с таким id не существует'", actual, equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Order cannot be accepted without orderId")
    public void testOrderCannotBeAcceptedWithoutOrderId() {
        ValidatableResponse acceptResponse = courierClient.acceptOrder(null, courierId);
        statusCode = acceptResponse.extract().statusCode();
        String actual = acceptResponse.extract().path("message");

        assertThat("Заказ можно принять без id заказа", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Нет сообщения 'Недостаточно данных для поиска'", actual, equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Order cannot be accepted with incorrect orderId")
    public void testOrderCannotBeAcceptedWithIncorrectOrderId() {
        ValidatableResponse acceptResponse = courierClient.acceptOrder("0", courierId);
        statusCode = acceptResponse.extract().statusCode();
        String actual = acceptResponse.extract().path("message");

        assertThat("Заказ можно принять с неверным id заказа", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Нет сообщения 'Заказа с таким id не существует'", actual, equalTo("Заказа с таким id не существует"));
    }

}
