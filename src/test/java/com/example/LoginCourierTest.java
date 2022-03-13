package com.example;

import io.restassured.response.ValidatableResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoginCourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private CourierCredentials courierCredentials;
    private ValidatableResponse loginResponse;
    private Integer courierId;
    private int statusCode;


    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = ScooterRegisterCourier.getRandom();
        courierClient.create(courier);
        courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
    }

    @After
    public void tearDown() {
        if(statusCode != SC_OK) {
            loginResponse = courierClient.login(courierCredentials);
            courierId = loginResponse.extract().path("id");
        }
        courierClient.delete(courierId.toString());
    }

    @Test
    public void testCourierCanLoginWithValidCredentials() {
        loginResponse = courierClient.login(courierCredentials);
        statusCode = loginResponse.extract().statusCode();
        courierId = loginResponse.extract().path("id");

        assertThat("Курьер не может залогиниться", statusCode, equalTo(SC_OK));
        assertThat("Некорректный ID курьера", courierId, is(not(0)));
    }

    @Test
    public void testCourierCannotLoginWithEmptyLogin() {
        loginResponse = courierClient.login(new CourierCredentials("", courier.getPassword()));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");

        assertThat("Курьер может залогиниться с пустым логином", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Нет сообщения 'Недостаточно данных для входа'", actual, equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void testCourierCannotLoginWithEmptyPasword() {
        loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), ""));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");

        assertThat("Курьер может залогиниться с пустым паролем", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Нет сообщения 'Недостаточно данных для входа'", actual, equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void testCourierCannotLoginWithEmptyCredentials() {
        loginResponse = courierClient.login(new CourierCredentials("", ""));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");

        assertThat("Курьер может залогиниться с пустыми логином и паролем", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Нет сообщения 'Недостаточно данных для входа'", actual, equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void testCourierCannotLoginWithNonExistentLogin() {
        loginResponse = courierClient.login(new CourierCredentials(courier.getLogin().substring(0,8), courier.getPassword()));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");

        assertThat("Курьер может залогиниться с несуществующим логином", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Нет сообщения 'Учетная запись не найдена'", actual, equalTo("Учетная запись не найдена"));
    }

    @Test
    public void testCourierCannotLoginWithNonExistentPassword() {
        loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword().substring(0,8)));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");

        assertThat("Курьер может залогиниться с несуществующим паролем", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Нет сообщения 'Учетная запись не найдена'", actual, equalTo("Учетная запись не найдена"));
    }

    @Test
    public void testCourierCannotLoginWithNonExistentCredentials() {
        loginResponse = courierClient.login(new CourierCredentials(courier.getLogin().substring(0,8), courier.getPassword().substring(0,8)));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");

        assertThat("Курьер может залогиниться с несуществующими логином и паролем", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Нет сообщения 'Учетная запись не найдена'", actual, equalTo("Учетная запись не найдена"));
    }

    @Test
    public void testCourierCannotLoginWithoutLogin() {
        loginResponse = courierClient.login(new CourierCredentials(null, courier.getPassword()));
        statusCode = loginResponse.extract().statusCode();
        assertThat("При отсутствии в JSON логина - ответ сервера не 400", statusCode, equalTo(SC_BAD_REQUEST));
        String actual = loginResponse.extract().path("message");
        assertThat("Нет сообщения 'Недостаточно данных для входа'", actual, equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void testCourierCannotLoginWithoutPassword() {
        loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), null));
        statusCode = loginResponse.extract().statusCode();
        assertThat("При отсутствии в JSON пароля - ответ сервера не 400", statusCode, equalTo(SC_BAD_REQUEST));
        String actual = loginResponse.extract().path("message");
        assertThat("Нет сообщения 'Недостаточно данных для входа'", actual, equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void testCourierCannotLoginWithoutCredentials() {
        loginResponse = courierClient.login(new CourierCredentials(null, null));
        statusCode = loginResponse.extract().statusCode();
        assertThat("При отсутствии в JSON логина и пароля - ответ сервера не 400", statusCode, equalTo(SC_BAD_REQUEST));
        String actual = loginResponse.extract().path("message");
        assertThat("Нет сообщения 'Недостаточно данных для входа'", actual, equalTo("Недостаточно данных для входа"));
    }
}