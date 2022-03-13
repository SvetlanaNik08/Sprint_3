package com.example;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateCourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private CourierCredentials courierCredentials;
    private Integer courierId;
    private int statusCode;
    private String login;
    private String password;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = ScooterRegisterCourier.getRandom();
        login = courier.getLogin();
        password = courier.getPassword();
        courierCredentials = new CourierCredentials(login, password);

    }

     @After
     public void tearDown() {
         if (statusCode != SC_BAD_REQUEST) {
             ValidatableResponse loginResponse = courierClient.login(courierCredentials);
             courierId = loginResponse.extract().path("id");
             courierClient.delete(courierId.toString());
         }
     }
    @Test
    public void testCourierCanBeCreated() {
        ValidatableResponse createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        boolean actual = createResponse.extract().path("ok");

        assertThat("Курьера нельзя создать", statusCode, equalTo(SC_CREATED));
        assertThat("Запрос не возвращает true", actual, is(true));
    }

    @Test
    public void testTheSameCourierCannotBeCreated() {
        ValidatableResponse createResponse = courierClient.create(courier);
        createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("Можно создать двух одинаковых курьеров", statusCode, equalTo(SC_CONFLICT));
        assertThat("Запрос не возвращает 'Этот логин уже используется'", actual, equalTo("Этот логин уже используется"));
    }

    @Test
    public void testCourierWithTheSameLoginCannotBeCreated() {
        ValidatableResponse createResponse = courierClient.create(courier);
        String newPassword = password.substring(0,8);
        courier.setPassword(newPassword);
        createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("Можно создать курьера с логином, который уже есть", statusCode, equalTo(SC_CONFLICT));
        assertThat("Запрос не возвращает 'Этот логин уже используется'", actual, equalTo("Этот логин уже используется"));
    }

    @Test
    public void testCourierCannotBeCreatedWithEmptyLogin() {
        courier.setLogin("");
        ValidatableResponse createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("Курьера можно создать с пустым логином", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Запрос не возвращает 'Недостаточно данных для создания учетной записи'", actual, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCourierCannotBeCreatedWithEmptyPassword() {
        courier.setPassword("");
        ValidatableResponse createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("Курьера можно создать с пустым паролем", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Запрос не возвращает 'Недостаточно данных для создания учетной записи'", actual, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCourierCannotBeCreatedWithEmptyCredentials() {
        courier.setLogin("");
        courier.setPassword("");
        ValidatableResponse createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("Курьера можно создать с пустыми логином и паролем", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Запрос не возвращает 'Недостаточно данных для создания учетной записи'", actual, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCourierCannotBeCreatedWithoutLogin() {
        courier.setLogin(null);
        ValidatableResponse createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("При отсутствии логина в JSON - ответ сервера не 400", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Запрос не возвращает 'Недостаточно данных для создания учетной записи'", actual, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCourierCannotBeCreatedWithoutPassword() {
        courier.setPassword(null);
        ValidatableResponse createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("При отсутствии пароля в JSON - ответ севера не 400", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Запрос не возвращает 'Недостаточно данных для создания учетной записи'", actual, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCourierCannotBeCreatedWithoutCredentials() {
        courier.setLogin(null);
        courier.setPassword(null);
        ValidatableResponse createResponse = courierClient.create(courier);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");

        assertThat("При отсутствии в JSON логина и пароля - ответ сервера не 400", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Запрос не возвращает 'Недостаточно данных для создания учетной записи'", actual, equalTo("Недостаточно данных для создания учетной записи"));
    }
}