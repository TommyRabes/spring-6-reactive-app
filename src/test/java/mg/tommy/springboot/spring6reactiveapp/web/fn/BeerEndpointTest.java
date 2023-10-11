package mg.tommy.springboot.spring6reactiveapp.web.fn;

import mg.tommy.springboot.spring6reactiveapp.mapper.BeerMapper;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer;
import mg.tommy.springboot.spring6reactiveapp.service.BeerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static mg.tommy.springboot.spring6reactiveapp.service.BeerServiceMongoTest.getTestBeer;
import static mg.tommy.springboot.spring6reactiveapp.web.fn.BeerRouter.BASE_PATH;
import static mg.tommy.springboot.spring6reactiveapp.web.fn.BeerRouter.BEER_PATH_ID;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureWebTestClient
public class BeerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    @Qualifier("beerServiceMongo")
    BeerService beerService;

    @Autowired
    BeerMapper mapper;

    @Test
    void patchBeerNotFoundTest() {
        webTestClient.patch()
                .uri(BEER_PATH_ID, 1000)
                .body(Mono.just(getTestBeer()), BeerDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void patchBeerTest() {
        BeerDto savedBeer = saveTestBeer();
        webTestClient.patch()
                .uri(BEER_PATH_ID, savedBeer.getId())
                .body(Mono.just(getTestBeer()), BeerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteBeerTest() {
        BeerDto savedBeer = saveTestBeer();
        webTestClient.delete()
                .uri(BEER_PATH_ID, savedBeer.getId())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(BEER_PATH_ID, savedBeer.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteBeerNotFoundTest() {
        webTestClient.delete()
                .uri(BEER_PATH_ID, 1000)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateBeerTest() {
        BeerDto savedBeer = saveTestBeer();
        webTestClient.put()
                .uri(BEER_PATH_ID, savedBeer.getId())
                .body(Mono.just(getTestBeer()), BeerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void badUpdateBeerTest() {
        BeerDto savedBeer = saveTestBeer();
        Beer badBeer = getTestBeer().toBuilder()
                .beerStyle("")
                .build();
        webTestClient.put()
                .uri(BEER_PATH_ID, savedBeer.getId())
                .body(Mono.just(badBeer), BeerDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createBeerTest() {
        webTestClient.post()
                .uri(BASE_PATH)
                .body(Mono.just(getTestBeer()), BeerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().value("location", startsWith(BASE_PATH));
    }

    @Test
    void createBadBeerTest() {
        Beer badBeer = getTestBeer().toBuilder()
                .beerName("")
                .build();
        webTestClient.post()
                .uri(BASE_PATH)
                .body(Mono.just(badBeer), BeerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByIdTest() {
        BeerDto savedBeer = saveTestBeer();
        webTestClient.get()
                .uri(BEER_PATH_ID, savedBeer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(BeerDto.class);
    }

    @Test
    void getByIdNotFoundTest() {
        webTestClient.get()
                .uri(BEER_PATH_ID, 1000)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listBeersTest() {
        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    void listBeersByStyleTest() {
        final String BEER_STYLE = "TEST";
        Beer beer = getTestBeer().toBuilder()
                .beerStyle(BEER_STYLE)
                .build();
        webTestClient.post()
                .uri(BASE_PATH)
                .body(Mono.just(beer), BeerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange();

        webTestClient.get()
                .uri(UriComponentsBuilder
                        .fromPath(BASE_PATH)
                        .queryParam("beerStyle", BEER_STYLE)
                        .build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    private BeerDto saveTestBeer() {
        /*
        FluxExchangeResult<BeerDto> beerDtoFluxExchangeResult = webTestClient.post().uri(BASE_PATH)
                .body(Mono.just(getTestBeer()), BeerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .returnResult(BeerDto.class);
        URI location = beerDtoFluxExchangeResult.getRequestHeaders().getLocation();

        return webTestClient.get()
                .uri(location)
                .exchange()
                .returnResult(BeerDto.class)
                .getResponseBody()
                .blockFirst();
        */
        return beerService.save(mapper.toDto(getTestBeer())).block();
    }
}
