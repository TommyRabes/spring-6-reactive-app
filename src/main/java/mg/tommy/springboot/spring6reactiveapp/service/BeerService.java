package mg.tommy.springboot.spring6reactiveapp.service;

import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {
    Flux<BeerDto> listBeers();

    Mono<BeerDto> getById(Integer id);

    Mono<BeerDto> save(BeerDto beerDto);

    Mono<BeerDto> update(Integer beerId, BeerDto beerDto);

    Mono<BeerDto> patch(Integer beerId, BeerDto beerDto);

    Mono<Void> delete(Integer beerId);
}
