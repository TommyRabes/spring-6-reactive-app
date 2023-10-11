package mg.tommy.springboot.spring6reactiveapp.web.fn;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.service.BeerService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static mg.tommy.springboot.spring6reactiveapp.web.fn.BeerRouter.BEER_PATH_ID;

@Component
@RequiredArgsConstructor
public class BeerHandler {

    @Qualifier("beerServiceMongo")
    private final BeerService beerService;
    private final Validator validator;

    public Mono<ServerResponse> listBeers(ServerRequest request) {
        Flux<BeerDto> flux;
        if (request.queryParam("beerStyle").isPresent()) {
            flux = beerService.getByBeerStyle(request.queryParam("beerStyle").get());
        }
        else {
            flux = beerService.listBeers();
        }
        return ServerResponse.ok()
                .body(flux, BeerDto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        return ServerResponse.ok()
                .body(beerService.getById(request.pathVariable("beerId"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        BeerDto.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(BeerDto.class)
                .doOnNext(this::validate)
                .flatMap(beerService::save)
                .flatMap(dto -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(BEER_PATH_ID)
                                .build(dto.getId())
                        )
                        .build()
                );
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return request.bodyToMono(BeerDto.class)
                .doOnNext(this::validate)
                .flatMap(beerDto -> beerService
                        .update(request.pathVariable("beerId"), beerDto))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patch(ServerRequest request) {
        return request.bodyToMono(BeerDto.class)
                .doOnNext(this::validate)
                .flatMap(beerDto -> beerService
                        .patch(request.pathVariable("beerId"), beerDto))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(patchedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        return beerService
                .getById(request.pathVariable("beerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(BeerDto::getId)
                .flatMap(beerService::delete)
                .then(ServerResponse.noContent().build());
    }

    private void validate(BeerDto beerDto) {
        Errors errors = new BeanPropertyBindingResult(beerDto, "beerDto");
        validator.validate(beerDto, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }
}
