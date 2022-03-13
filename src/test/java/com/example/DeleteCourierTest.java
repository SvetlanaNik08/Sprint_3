package com.example;
import io.restassured.response.ValidatableResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DeleteCourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;
    private int statusCode;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = ScooterRegisterCourier.getRandom();
        courierClient.create(courier);
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.extract().path("id");
     }

     @After
     public void tearDown() {
         if(statusCode != SC_OK) {
             courierClient.delete(courierId.toString());
         }
     }

    @Test
    public void testCourierCanBeDeleted() {
        ValidatableResponse deleteResponse = courierClient.delete(courierId.toString());
        statusCode = deleteResponse.extract().statusCode();
        boolean actual = deleteResponse.extract().path("ok");

        assertThat("Курьера нельзя удалить", statusCode, equalTo(SC_OK));
        assertThat("Запрос не возвращает true", actual, is(true));
    }

    @Test
    public void testCourierCannotBeDeletedWithoutId() {
        ValidatableResponse deleteResponse = courierClient.delete(null);
        statusCode = deleteResponse.extract().statusCode();
        String actual = deleteResponse.extract().path("message");

        assertThat("При удалении курьера без id - ответ сервера не 400", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Запрос не возвращает 'Недостаточно данных для удаления курьера'", actual, equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    public void testNonExistentCourierCannotBeDeleted() {
        ValidatableResponse deleteResponse = courierClient.delete("0");
        statusCode = deleteResponse.extract().statusCode();
        String actual = deleteResponse.extract().path("message");

        assertThat("Можно удалить несуществующего курьера", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Запрос не возвращает 'Курьера с таким id нет'", actual, equalTo("Курьера с таким id нет"));
    }
}
