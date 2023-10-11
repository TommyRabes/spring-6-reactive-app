package mg.tommy.springboot.spring6reactiveapp.bootstrap;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Beer;
import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Customer;
import mg.tommy.springboot.spring6reactiveapp.repository.h2.BeerRepository;
import mg.tommy.springboot.spring6reactiveapp.repository.h2.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final mg.tommy.springboot.spring6reactiveapp.repository.mongo.BeerRepository beerMongoRepository;
    private final mg.tommy.springboot.spring6reactiveapp.repository.mongo.CustomerRepository customerMongoRepository;

    @Override
    public void run(String... args) throws InterruptedException {
        // Calling the Reactive Mongo repository with Mono.zip() doesn't work well (bug ?)
        // So fallback to sequential execution
        callDeleteBeer().then(callDeleteBeerMongo())
                .doOnSuccess(success -> {
                    System.out.println("On success");
                    System.out.println(success);
                    loadBeerData();
                    loadBeerMongoData();
                })
                .subscribe();

        customerRepository.deleteAll()
                        .then(loadCustomerData());

        beerRepository.count().subscribe(count -> {
            System.out.println("Count is: " + count);
        });
    }

    // Wrapper methods just for debugging purpose
    private Mono<Void> callDeleteBeer() {
        return beerRepository.deleteAll().doOnSubscribe((subscription) -> {
            System.out.println("Deleting all beers");
        })
                .doOnSuccess(success -> System.out.println("Success: Deleting all beers"))
                .doOnError(error -> System.out.println("Error: Deleting all beers - " + error.getMessage()))
                .doOnTerminate(() -> System.out.println("Terminated: Deleting all beers"));
    }

    private Mono<Void> callDeleteBeerMongo() {
        return beerMongoRepository.deleteAll().doOnSubscribe((subscription) -> {
            System.out.println("Deleting all mongo beers");
        })
                .doOnSuccess(success -> System.out.println("Success: Deleting all mongo beers"))
                .doOnError(error -> System.out.println("Error: Deleting all mongo beers - " + error.getMessage()))
                .doOnTerminate(() -> System.out.println("Terminated: Deleting all mongo beers"));
    }

    private Mono<Long> loadCustomerData() {
        return customerRepository.count().doOnSuccess(count -> {
            if (count > 0) return;

            customerRepository.saveAll(
                    Arrays.asList(
                            Customer.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .email("john.doe@gmail.com")
                                .birthdate(LocalDate.of(1987, 2, 13))
                                .build(),
                            Customer.builder()
                                    .firstName("Jane")
                                    .lastName("Smith")
                                    .email("jane.smith@gmail.com")
                                    .birthdate(LocalDate.of(1995, 12, 5))
                                    .build(),
                            Customer.builder()
                                    .firstName("Ross")
                                    .lastName("Stinger")
                                    .email("ross.stinger@gmail.com")
                                    .birthdate(LocalDate.of(1990, 7, 30))
                                    .build()
                    )
            ).subscribe();
        });
    }

    private void loadBeerData() {
        beerRepository.count().subscribe(count -> {
            if (count > 1000) return;

            beerRepository.save(Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle("PALE_ALE")
                    .upc("06546127")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(250)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build()).subscribe();

            beerRepository.save(Beer.builder()
                    .beerName("Crank")
                    .beerStyle("PALE_ALE")
                    .upc("168673")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(190)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build()).subscribe();

            beerRepository.save(Beer.builder()
                    .beerName("Sunshine City")
                    .beerStyle("IPA")
                    .upc("98726454")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(150)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build()).subscribe();

        });
    }

    private void loadBeerMongoData() {
        beerMongoRepository.count().subscribe(count -> {
            if (count > 1000) return;

            beerMongoRepository.save(mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle("PALE_ALE")
                    .upc("06546127")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(250)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build()).subscribe();

            beerMongoRepository.save(mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer.builder()
                    .beerName("Crank")
                    .beerStyle("PALE_ALE")
                    .upc("168673")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(190)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build()).subscribe();

            beerMongoRepository.save(mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer.builder()
                    .beerName("Sunshine City")
                    .beerStyle("IPA")
                    .upc("98726454")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(150)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build()).subscribe();

        });
    }
}
