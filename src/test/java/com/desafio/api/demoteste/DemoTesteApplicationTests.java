package com.desafio.api.demoteste;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@Slf4j
public class DemoTesteApplicationTests extends IntegrationTest {

	private final String uriBase = "https://darwin-api.continuousplatform.com/moove";

	FileInputStream fileInputStream;

	@Test
	public void post_token_without_password() {
		RestAssured.given()
				.config(RestAssured.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)))
				.contentType("application/x-www-form-urlencoded; charset=UTF-8")
				.formParam("username", "processoqa@zup.com.br")
				.formParam("password", "")
				.formParam("grant_type", "password")
				.formParam("client_id", "darwin-client")
				.when()
				.post(getUriAuth() + "/auth/realms/darwin/protocol/openid-connect/token")
				.then()
				.statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void post_create_circles_valid() throws IOException {
		log.info("**Validando criação de um circle válido**");
		fileInputStream = new FileInputStream(new File("src/test/resources/circle_darwin.json"));
		RestAssured.given()
					.contentType(ContentType.JSON)
					.header("Authorization", "Bearer " + getAuthorizationHeader())
					.body(IOUtils.toString(fileInputStream, "UTF-8"))
				.when()
					.post(uriBase + "/circles")
				.then()
					.statusCode(HttpStatus.SC_OK)
					.body("name", is("My circle"));
	}

	@Test
	public void post_create_circles_unauthorized() throws IOException {
		log.info("**Validando tentativa de criação de circle sem autorização**");
		fileInputStream = new FileInputStream(new File("src/test/resources/circle_darwin.json"));
		RestAssured.given()
					.contentType(ContentType.JSON)
					.body(IOUtils.toString(fileInputStream, "UTF-8"))
				.when()
					.post(uriBase + "/circles")
				.then()
					.statusCode(HttpStatus.SC_UNAUTHORIZED)
					.body(containsString("Key not authorised"));
	}

	@Test
	public void get_builds_valid() throws Exception {
		log.info("**Validando obtenção de builds**");
		RestAssured.given()
					.param("tagName", "")
					.param("page", "0")
					.param("status", "BUILT")
					.header("Authorization", "Bearer " + getAuthorizationHeader())
					.header("x-application-id", "900c4342-df7e-488b-bf6f-1784ee7c0546")
					.contentType(ContentType.JSON)
				.when()
					.get(uriBase + "/builds")
				.then()
					.statusCode(HttpStatus.SC_OK)
					.body("page", equalTo(0));
	}

	@Test
	public void get_builds_unauthorized() {
		log.info("**Validando tentativa de obtenção de builds sem autorização**");
		RestAssured.given()
					.param("tagName", "")
					.param("page", "0")
					.param("status", "BUILT")
					.header("x-application-id", "900c4342-df7e-488b-bf6f-1784ee7c0546")
					.contentType(ContentType.JSON)
				.when()
					.get(uriBase + "/builds")
				.then()
					.statusCode(HttpStatus.SC_UNAUTHORIZED)
					.body(containsString("Key not authorised"));
	}

}
