package mg.tommy.springboot.spring6reactiveapp.web.fn;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import mg.tommy.springboot.spring6reactiveapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static mg.tommy.springboot.spring6reactiveapp.web.fn.CustomerRouter.CUSTOMER_PATH_ID;

@Component
@RequiredArgsConstructor
public class CustomerHandler {
    @Qualifier("customerServiceMongo")
    private final CustomerService customerService;
    private final Validator validator;

    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        return ServerResponse.ok()
                .body(customerService.list(), CustomerDto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        return ServerResponse.ok()
                .body(customerService.findById(request.pathVariable("customerId"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        CustomerDto.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request
                .bodyToMono(CustomerDto.class)
                .doOnNext(this::validate)
                .flatMap(customerService::save)
                .flatMap(savedDto -> ServerResponse.created(UriComponentsBuilder
                        .fromPath(CUSTOMER_PATH_ID)
                        .build(savedDto.getId()))
                        .build()
                );
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return request
                .bodyToMono(CustomerDto.class)
                .doOnNext(this::validate)
                .flatMap(dto -> customerService.update(request.pathVariable("customerId"), dto))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patch(ServerRequest request) {
        return request
                .bodyToMono(CustomerDto.class)
                .doOnNext(this::validate)
                .flatMap(dto -> customerService.patch(request.pathVariable("customerId"), dto))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        return customerService
                .findById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(CustomerDto::getId)
                .flatMap(customerService::delete)
                .then(ServerResponse.noContent().build());
    }

    private void validate(CustomerDto customerDto) {
        Errors errors = new BeanPropertyBindingResult(customerDto, "customerDto");

        validator.validate(customerDto, errors);
        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }
}
