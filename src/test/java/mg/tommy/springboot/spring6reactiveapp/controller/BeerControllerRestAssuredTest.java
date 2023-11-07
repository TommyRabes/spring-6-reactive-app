package mg.tommy.springboot.spring6reactiveapp.controller;

import io.restassured.http.ContentType;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import mg.tommy.springboot.spring6reactiveapp.web.fn.BeerRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;


@SpringBootTest
@AutoConfigureWebTestClient
public class BeerControllerRestAssuredTest {

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RestAssuredWebTestClient.webTestClient(webTestClient.mutateWith(mockOAuth2Login()));
    }

    @Test
    void listBeersTest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(BeerRouter.BASE_PATH)
                .then()
                .assertThat().statusCode(200);
    }
}
