package mg.tommy.springboot.spring6reactiveapp.service;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.mapper.CustomerMapper;
import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Customer;
import mg.tommy.springboot.spring6reactiveapp.repository.mongo.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerServiceMongo implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    @Override
    public Flux<CustomerDto> list() {
        return repository.findAll()
                .map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> findById(String customerId) {
        return repository.findById(customerId)
                .map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> save(CustomerDto dto) {
        return repository.save(mapper.toNoSqlCustomer(dto))
                .map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> update(String id, CustomerDto dto) {
        return repository.findById(id)
                .map(customerToUpdate -> customerToUpdate.toBuilder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .birthdate(dto.getBirthdate())
                        .build()
                )
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> patch(String id, CustomerDto dto) {
        return repository.findById(id)
                .map(customerToPatch -> {
                    Customer.CustomerBuilder builder = customerToPatch.toBuilder();
                    if (StringUtils.hasText(dto.getFirstName())) {
                        builder.firstName(dto.getFirstName());
                    }
                    if (StringUtils.hasText(dto.getLastName())) {
                        builder.lastName(dto.getLastName());
                    }
                    if (StringUtils.hasText(dto.getEmail())) {
                        builder.email(dto.getEmail());
                    }
                    if (Objects.nonNull(dto.getBirthdate())) {
                        builder.birthdate(dto.getBirthdate());
                    }
                    return builder.build();
                })
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}
