package mg.tommy.springboot.spring6reactiveapp.controller;

import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Beer;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.repository.BeerRepositoryTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.startsWith;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void listBeersTest() {
        webTestClient.get().uri(BeerController.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    void getByIdTest() {
        webTestClient.get().uri(BeerController.BEER_PATH + "/{beerId}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody(BeerDto.class);
    }

    @Test
    void updateBeerTest() {
        webTestClient.put().uri(BeerController.BEER_PATH + "/{beerId}", 1)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void patchBeerTest() {
        webTestClient.patch().uri(BeerController.BEER_PATH + "/{beerId}", 1)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    // Spring boot TestContext framework doesn't support R2dbcTransactionManager (only PlatformTransactionManager)
    // Therefore, we can't use @Transactional in a reactive test for the time being
    // We could create a helper method/class to circumvent the issue, but we will stick to running this test last for now
    @Test
    @Order(1)
    void createBeerTest() {
        webTestClient.post().uri(BeerController.BEER_PATH)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().value("location", startsWith("http://localhost:8080/api/v2/beers/"));
    }

    @Test
    @Order(2)
    void deleteBeerTest() {
        webTestClient.delete().uri(BeerController.BEER_PATH + "/{beerId}", 4)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void createBadBeerTest() {
        Beer badBeer = BeerRepositoryTest.getTestBeer().toBuilder().beerName("").build();

        webTestClient.post().uri(BeerController.BEER_PATH)
                .body(Mono.just(badBeer), BeerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void badUpdateBeerTest() {
        Beer badBeer = BeerRepositoryTest.getTestBeer().toBuilder().beerStyle("").build();
        webTestClient.put().uri(BeerController.BEER_PATH + "/{beerId}", 1)
                .body(Mono.just(badBeer), BeerDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByIdNotFoundTest() {
        webTestClient.get().uri(BeerController.BEER_PATH + "/{beerId}", 100)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateBeerNotFoundTest() {
        webTestClient.put().uri(BeerController.BEER_PATH + "/{beerId}", 100)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void patchBeerNotFoundTest() {
        webTestClient.patch().uri(BeerController.BEER_PATH + "/{beerId}", 100)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteBeerNotFoundTest() {
        webTestClient.delete().uri(BeerController.BEER_PATH + "/{beerId}", 100)
                .exchange()
                .expectStatus().isNotFound();
    }

}