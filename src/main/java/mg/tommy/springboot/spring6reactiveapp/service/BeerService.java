package mg.tommy.springboot.spring6reactiveapp.service;

import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {
    Flux<BeerDto> listBeers();

    Mono<BeerDto> getById(String id);
    Mono<BeerDto> getByBeerName(String beerName);
    Flux<BeerDto> getByBeerStyle(String beerStyle);

    Mono<BeerDto> save(BeerDto beerDto);

    Mono<BeerDto> update(String beerId, BeerDto beerDto);

    Mono<BeerDto> patch(String beerId, BeerDto beerDto);

    Mono<Void> delete(String beerId);
}
