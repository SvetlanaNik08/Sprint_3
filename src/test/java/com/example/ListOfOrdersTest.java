package com.example;

import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ListOfOrdersTest {
    private CourierClient courierClient;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("List of orders can be received")
    public void testListOfOrdersCanBeReceived() {
        ValidatableResponse listResponse = courierClient.getListOfOrders();
        int statusCode = listResponse.extract().statusCode();
        ArrayList<Integer> ordersId = listResponse.extract().path("orders.id");
        int ordersCount = listResponse.extract().path("pageInfo.total");
        ArrayList<String> stationNameList = listResponse.extract().path("availableStations.name");

        assertThat("Список заказов не может быть получен", statusCode, equalTo(SC_OK));
        assertThat("Список заказов имеет пустой блок orders", ordersId.size(), is(not(0)));
        assertThat("В блоке pageInfo списка заказов - поле total равно 0", ordersCount, is(not(0)));
        assertThat("Список заказов имеет пустой блок availableStations", stationNameList.size(), is(not(0)));
    }
}
