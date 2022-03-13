package com.example;
import io.restassured.response.ValidatableResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetOrderByTrackTest {
    private CourierClient courierClient;
    private int statusCode;
    private Order order;
    private Integer track;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        order = new Order(List.of(""));
        ValidatableResponse orderResponse = courierClient.createOrder(order);
        track = orderResponse.extract().path("track");
    }

    @After
    public void tearDown() {
        courierClient.cancelOrder(track.toString());
    }

    @Test
    public void testCanGetOrderByTrack() {
        ValidatableResponse getOrderResponse = courierClient.getOrderByTrack(track);
        statusCode = getOrderResponse.extract().statusCode();
        int orderId = getOrderResponse.extract().path("order.id");
        assertThat("Заказ нельзя получить по его номеру", statusCode, equalTo(SC_OK));
        assertThat("Некорректный номер заказа", orderId, is(not(0)));
    }

    @Test
    public void testCanotGetOrderWithoutTrack() {
        ValidatableResponse getOrderResponse = courierClient.getOrderByTrack(null);
        statusCode = getOrderResponse.extract().statusCode();
        String actual = getOrderResponse.extract().path("message");
        assertThat("Заказ можно получить без номера track", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Нет сообщения 'Недостаточно данных для поиска'", actual, equalTo("Недостаточно данных для поиска"));
    }

    @Test
    public void testCannotGetOrderWithIncorrectTrack() {
        ValidatableResponse getOrderResponse = courierClient.getOrderByTrack(0);
        statusCode = getOrderResponse.extract().statusCode();
        String actual = getOrderResponse.extract().path("message");
        assertThat("Заказ можно получить по несуществующему номеру", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Нет сообщения 'Заказ не найден'", actual, equalTo("Заказ не найден"));
    }
}
