package mg.tommy.springboot.spring6reactiveapp.bootstrap;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.model.domain.Beer;
import mg.tommy.springboot.spring6reactiveapp.model.domain.Customer;
import mg.tommy.springboot.spring6reactiveapp.repository.BeerRepository;
import mg.tommy.springboot.spring6reactiveapp.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadBeerData();
        loadCustomerData();
        beerRepository.count().subscribe(count -> {
            System.out.println("Count is: " + count);
        });
    }

    private void loadCustomerData() {
        customerRepository.count().subscribe(count -> {
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
}
