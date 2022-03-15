package com.example;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class CourierClient extends ScooterRestClient {
    private static final String COURIER_PATH = "/api/v1/courier/";
    private static final String ORDER_PATH = "/api/v1/orders/";

    @Step("Login as a courier")
    public ValidatableResponse login(CourierCredentials credentials) {
       return given()
               .spec(getBaseSpec())
               .body(credentials)
               .when()
               .post(COURIER_PATH + "login")
               .then();
    }

    @Step("Courier creation")
    public ValidatableResponse create(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }

    @Step("Deleting a courier with id = {id}")
    public ValidatableResponse delete(String id) {
        if (id == null) {
            return given()
                    .spec(getBaseSpec())
                    .when()
                    .delete(COURIER_PATH)
                    .then();
        } else {
            return given()
                    .spec(getBaseSpec())
                    .when()
                    .delete(COURIER_PATH + id)
                    .then();
        }
    }

    @Step("Order creation")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Getting a list of orders")
    public ValidatableResponse getListOfOrders() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Cancellation of the order with track = {track}")
    public ValidatableResponse cancelOrder(String track) {
        return given()
                .spec(getBaseSpec())
                .body(track)
                .when()
                .put(ORDER_PATH + "cancel")
                .then();
    }

    @Step("Getting orderId by track = {track}")
    public ValidatableResponse getOrderByTrack(Integer track) {
        return given()
                .spec(getBaseSpec())
                .queryParam("t", track)
                .when()
                .get(ORDER_PATH + "track")
                .then();
    }

    @Step("Courier accepts an order")
    public ValidatableResponse acceptOrder(String orderId, String courierId) {
        if (orderId == null) {
            return given()
                    .spec(getBaseSpec())
                    .put(ORDER_PATH + "accept/" + "courierId =" + courierId)
                    .then();
        } else {
            return given()
                    .spec(getBaseSpec())
                    .queryParam("courierId", courierId)
                    .put(ORDER_PATH + "accept/" + orderId)
                    .then();
        }
    }
}
