package mg.tommy.springboot.spring6reactiveapp.controller;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import mg.tommy.springboot.spring6reactiveapp.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_PATH)
public class CustomerController {
    protected static final String BASE_PATH = "/api/v2/customers";

    private final CustomerService customerService;

    @GetMapping
    Flux<CustomerDto> listCustomers() {
        return customerService.list();
    }

    @GetMapping("{customerId}")
    Mono<ResponseEntity<CustomerDto>> getById(@PathVariable("customerId") String customerId) {
        return customerService.findById(customerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(ResponseEntity::ok);
    }

    @PostMapping
    Mono<ResponseEntity<Void>> save(@Validated @RequestBody CustomerDto customer) {
        return customerService.save(customer)
                .map(savedCustomer -> ResponseEntity
                        .created(UriComponentsBuilder
                                .fromHttpUrl("http://localhost:8080" + BASE_PATH + "/{customerId}")
                                .build(savedCustomer.getId()))
                        .build());
    }

    @PutMapping("{customerId}")
    Mono<ResponseEntity<Void>> update(@PathVariable("customerId") String id,
                                      @Validated @RequestBody CustomerDto dto) {
        return customerService.update(id, dto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(updatedCustomer -> ResponseEntity.noContent().build());
    }

    @PatchMapping("{customerId}")
    Mono<ResponseEntity<Void>> patch(@PathVariable("customerId") String id,
                                     @Validated @RequestBody CustomerDto dto) {
        return customerService.patch(id, dto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(updatedCustomer -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("{customerId}")
    Mono<ResponseEntity<Void>> delete(@PathVariable("customerId") String id) {
        return customerService.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(CustomerDto::getId)
                .flatMap(customerService::delete)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
