package com.example;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private CourierClient courierClient;
    private Order order;
    private Integer track;
    private final List<String> listOfColors;

    public CreateOrderTest(String listOfColors) {
        this.listOfColors = List.of(listOfColors);
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
        return new Object[][]{
                {"\"BLACK\", \"GREY\""},
                {"\"GREY\""},
                {"\"BLACK\""},
                {""},
        };
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        order = new Order(listOfColors);
    }

    @After
    public void tearDown() {
        courierClient.cancelOrder(track.toString());
    }

    @Test
    @DisplayName("Order can be created")
    public void testOrderCanBeCreated() {
        ValidatableResponse createResponse = courierClient.createOrder(order);
        int statusCode = createResponse.extract().statusCode();
        track = createResponse.extract().path("track");

        assertThat("Заказ нельзя создать", statusCode, equalTo(SC_CREATED));
        assertThat("Некорректный номер заказа", track, is(not(0)));
    }
}
