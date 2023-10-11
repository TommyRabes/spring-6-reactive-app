package mg.tommy.springboot.spring6reactiveapp.service;

import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class CustomerServiceMongoTest {
    @Qualifier("customerServiceMongo")
    @Autowired
    CustomerService customerService;

    @Test
    void listCustomersTest() {
        saveTestCustomer();
        AtomicReference<List<CustomerDto>> atomicCustomers = new AtomicReference<>();
        customerService.list()
                .collectList()
                .subscribe(atomicCustomers::set);

        await().until(() -> atomicCustomers.get() != null);

        assertThat(atomicCustomers.get()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void getCustomerByIdTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        AtomicReference<CustomerDto> atomicCustomer = new AtomicReference<>();
        customerService.findById(savedCustomer.getId())
                .subscribe(atomicCustomer::set);

        await().until(() -> atomicCustomer.get() != null);

        assertThat(atomicCustomer.get().getId()).isEqualTo(savedCustomer.getId());
    }

    @Test
    void saveCustomerTest() {
        CustomerDto customerToSave = getTestCustomer();
        AtomicReference<CustomerDto> atomicCustomer = new AtomicReference<>();
        customerService.save(customerToSave)
                .subscribe(atomicCustomer::set);

        await().until(() -> atomicCustomer.get() != null);

        assertThat(atomicCustomer.get().getId()).isNotNull();
        assertThat(atomicCustomer.get().getId()).isNotEmpty();
    }

    @Test
    void updateCustomerTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        final String FIRST_NAME = "Jean";
        CustomerDto customerToUpdate = savedCustomer.toBuilder()
                .firstName(FIRST_NAME)
                .build();
        AtomicReference<CustomerDto> atomicCustomer = new AtomicReference<>();
        customerService.update(customerToUpdate.getId(), customerToUpdate)
                .subscribe(atomicCustomer::set);

        await().until(() -> atomicCustomer.get() != null);

        assertThat(atomicCustomer.get().getId()).isEqualTo(savedCustomer.getId());
        assertThat(atomicCustomer.get().getFirstName()).isEqualTo(FIRST_NAME);
    }

    @Test
    void deleteCustomerTest() {
        CustomerDto savedCustomer = saveTestCustomer();
        AtomicBoolean isCompleted = new AtomicBoolean(false);
        customerService.delete(savedCustomer.getId())
                .doOnSuccess(none -> isCompleted.set(true))
                .subscribe();

        await().untilTrue(isCompleted);
        Mono<CustomerDto> empty = customerService.findById(savedCustomer.getId());
        StepVerifier.create(empty).expectNextCount(0).verifyComplete();
    }
    
    private CustomerDto saveTestCustomer() {
        return customerService.save(getTestCustomer()).block();
    }

    public static CustomerDto getTestCustomer() {
        return CustomerDto.builder()
                .firstName("Franck")
                .lastName("René")
                .email("franck@gmail.com")
                .birthdate(LocalDate.of(1990, 6, 6))
                .build();
    }
}
