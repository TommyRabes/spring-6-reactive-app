package mg.tommy.springboot.spring6reactiveapp.service;

import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDto> list();

    Mono<CustomerDto> findById(String customerId);

    Mono<CustomerDto> save(CustomerDto dto);

    Mono<CustomerDto> update(String id, CustomerDto dto);

    Mono<CustomerDto> patch(String id, CustomerDto dto);

    Mono<Void> delete(String id);
}
