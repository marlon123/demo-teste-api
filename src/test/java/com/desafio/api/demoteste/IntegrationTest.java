package com.desafio.api.demoteste;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.containsString;

public abstract class IntegrationTest {

    private final String uriAuth = "https://darwin-keycloak.continuousplatform.com";

    protected String getAuthorizationHeader() {
        String token = RestAssured.given()
                    .config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)))
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .formParam("username", "processoqa@zup.com.br")
                    .formParam("password", "123mudar")
                    .formParam("grant_type", "password")
                    .formParam("client_id", "darwin-client")
                .when()
                    .post(uriAuth + "/auth/realms/darwin/protocol/openid-connect/token")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body(containsString("access_token"))
                    .extract().path("access_token");
        return token;
    }

    public String getUriAuth(){
        return uriAuth;
    }
}
