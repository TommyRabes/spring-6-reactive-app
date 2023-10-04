package mg.tommy.springboot.spring6reactiveapp.controller;

import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static mg.tommy.springboot.spring6reactiveapp.controller.CustomerController.BASE_PATH;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class CustomerControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void listCustomersTest() {
        webTestClient.get().uri(BASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    void getByIdTest() {
        webTestClient.get().uri(BASE_PATH + "/{beerId}", 1)
                .exchange()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectStatus().isOk()
                .expectBody(BeerDto.class);
    }

    @Test
    @Order(1)
    void createCustomerTest() {
        webTestClient.post().uri(BASE_PATH)
                .body(Mono.just(createCustomerInstance()), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost:8080" + BASE_PATH + "/4");
    }

    @Test
    @Order(2)
    void deleteCustomerTest() {
        webTestClient.delete().uri(BASE_PATH + "/{beerId}", 4)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateCustomerTest() {
        webTestClient.put().uri(BASE_PATH + "/{customerId}", 1)
                .body(Mono.just(createCustomerInstance()), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void patchCustomerTest() {
        webTestClient.patch().uri(BASE_PATH + "/{customerId}", 1)
                .body(Mono.just(createCustomerInstance()), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getByIdNotFoundTest() {
        webTestClient.get().uri(BASE_PATH + "/{beerId}", 100)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createBadCustomerTest() {
        CustomerDto badCustomer = createCustomerInstance().toBuilder().firstName("").build();
        webTestClient.post().uri(BASE_PATH)
                .body(Mono.just(badCustomer), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void badUpdateCustomerTest() {
        CustomerDto badCustomer = createCustomerInstance().toBuilder().firstName("").build();
        webTestClient.put().uri(BASE_PATH + "/{customerId}", 1)
                .body(Mono.just(badCustomer), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateCustomerNotFoundTest() {
        webTestClient.put().uri(BASE_PATH + "/{customerId}", 100)
                .body(Mono.just(createCustomerInstance()), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void patchCustomerNotFoundTest() {
        webTestClient.patch().uri(BASE_PATH + "/{customerId}", 100)
                .body(Mono.just(createCustomerInstance()), CustomerDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteCustomerNotFoundTest() {
        webTestClient.delete().uri(BASE_PATH + "/{beerId}", 100)
                .exchange()
                .expectStatus().isNotFound();
    }

    private CustomerDto createCustomerInstance() {
        return CustomerDto.builder()
                .firstName("Dylan")
                .lastName("Perez")
                .email("pylan@gmail.com")
                .birthdate(LocalDate.of(2004, 2, 6))
                .build();
    }
}
