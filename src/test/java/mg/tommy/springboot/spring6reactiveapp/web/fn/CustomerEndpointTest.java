package mg.tommy.springboot.spring6reactiveapp.web.fn;

import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import mg.tommy.springboot.spring6reactiveapp.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static mg.tommy.springboot.spring6reactiveapp.service.CustomerServiceMongoTest.getTestCustomer;
import static mg.tommy.springboot.spring6reactiveapp.web.fn.CustomerRouter.BASE_PATH;
import static mg.tommy.springboot.spring6reactiveapp.web.fn.CustomerRouter.CUSTOMER_PATH_ID;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    @Qualifier("customerServiceMongo")
    CustomerService customerService;

    @Test
    void patchCustomerNotFoundTest() {
        webTestClient.patch()
                .uri(CUSTOMER_PATH_ID, 1000)
                .body(Mono.just(getTestCustomer()), CustomerDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void patchCustomerTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        webTestClient.patch()
                .uri(CUSTOMER_PATH_ID, savedCustomer.getId())
                .body(Mono.just(getTestCustomer()), CustomerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteCustomerTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        webTestClient.delete()
                .uri(CUSTOMER_PATH_ID, savedCustomer.getId())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(CUSTOMER_PATH_ID, savedCustomer.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteCustomerNotFoundTest() {
        webTestClient.delete()
                .uri(CUSTOMER_PATH_ID, 1000)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateCustomerTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        webTestClient.put()
                .uri(CUSTOMER_PATH_ID, savedCustomer.getId())
                .body(Mono.just(getTestCustomer()), CustomerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void badUpdateCustomerTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        CustomerDto badCustomer = savedCustomer.toBuilder()
                .firstName("")
                .build();
        webTestClient.put()
                .uri(CUSTOMER_PATH_ID, savedCustomer.getId())
                .body(Mono.just(badCustomer), CustomerDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createCustomerTest() {
        webTestClient.post()
                .uri(BASE_PATH)
                .body(Mono.just(getTestCustomer()), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().value("location", startsWith(BASE_PATH));
    }

    @Test
    void createBadCustomerTest() {
        CustomerDto badCustomer = getTestCustomer().toBuilder()
                .email("notanemailformat")
                .build();
        webTestClient.post()
                .uri(BASE_PATH)
                .body(Mono.just(badCustomer), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByIdTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        webTestClient.get()
                .uri(CUSTOMER_PATH_ID, savedCustomer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(CustomerDto.class);
    }

    @Test
    void getByIdNotFoundTest() {
        webTestClient.get()
                .uri(CUSTOMER_PATH_ID, 1000)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listCustomersTest() {
        webTestClient.get()
                .uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    private CustomerDto saveTestCustomer() {
        /*
        FluxExchangeResult<CustomerDto> beerDtoFluxExchangeResult = webTestClient.post().uri(BASE_PATH)
                .body(Mono.just(getTestCustomer()), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .returnResult(CustomerDto.class);
        URI location = beerDtoFluxExchangeResult.getRequestHeaders().getLocation();

        return webTestClient.get()
                .uri(location)
                .exchange()
                .returnResult(CustomerDto.class)
                .getResponseBody()
                .blockFirst();
        */
        return customerService.save(getTestCustomer()).block();
    }
}
