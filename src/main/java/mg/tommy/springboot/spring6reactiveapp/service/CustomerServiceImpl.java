package mg.tommy.springboot.spring6reactiveapp.service;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.mapper.CustomerMapper;
import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Customer;
import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import mg.tommy.springboot.spring6reactiveapp.repository.h2.CustomerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Primary
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;
    @Override
    public Flux<CustomerDto> list() {
        return customerRepository.findAll().map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> findById(String customerId) {
        return customerRepository.findById(Integer.valueOf(customerId))
                .map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> save(CustomerDto dto) {
        return customerRepository.save(mapper.toSqlCustomer(dto))
                .map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> update(String id, CustomerDto dto) {
        return customerRepository.findById(Integer.valueOf(id))
                .map(customerToUpdate -> customerToUpdate.toBuilder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .birthdate(dto.getBirthdate())
                        .build())
                .flatMap(customerRepository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<CustomerDto> patch(String id, CustomerDto dto) {
        return customerRepository.findById(Integer.valueOf(id))
                .map(customerToUpdate -> {
                    Customer.CustomerBuilder builder = customerToUpdate.toBuilder();
                    if (StringUtils.hasText(dto.getFirstName()))
                        builder.firstName(dto.getFirstName());
                    if (StringUtils.hasText(dto.getLastName()))
                        builder.lastName(dto.getLastName());
                    if (StringUtils.hasText(dto.getEmail()))
                        builder.email(dto.getEmail());
                    if (Objects.nonNull(dto.getBirthdate()))
                        builder.birthdate(dto.getBirthdate());

                    return builder.build();
                })
                .flatMap(customerRepository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.deleteById(Integer.valueOf(id));
    }
}
