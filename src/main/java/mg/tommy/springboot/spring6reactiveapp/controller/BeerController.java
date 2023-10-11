package mg.tommy.springboot.spring6reactiveapp.controller;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.service.BeerService;
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
@RequestMapping(BeerController.BEER_PATH)
public class BeerController {

    protected static final String BEER_PATH = "/api/v2/beers";

    private final BeerService beerService;

    @GetMapping
    Flux<BeerDto> listBeers() {
        return beerService.listBeers();
    }

    @GetMapping("{beerId}")
    Mono<BeerDto> getBeerById(@PathVariable("beerId") String id) {
        return beerService.getById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping
    Mono<ResponseEntity<Void>> saveBeer(@Validated @RequestBody BeerDto beerDto) {
        return beerService.save(beerDto)
                .map(savedDto -> ResponseEntity.created(UriComponentsBuilder
                        .fromHttpUrl("http://localhost:8080" + BEER_PATH + "/" + savedDto.getId())
                        .build().toUri()
                ).build());
    }

    @PutMapping("{beerId}")
    Mono<ResponseEntity<Void>> updateBeer(@PathVariable("beerId") String beerId,
                                          @Validated @RequestBody BeerDto beerDto) {
        return beerService.update(beerId, beerDto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(updatedBeer -> ResponseEntity.noContent().build());
    }

    @PatchMapping("{beerId}")
    Mono<ResponseEntity<Void>> patchBeer(@PathVariable("beerId") String beerId,
                                         // Needs to use a different validation group for this patch operation
                                         @Validated @RequestBody BeerDto beerDto) {
        return beerService.patch(beerId, beerDto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(patchedBeer -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("{beerId}")
    Mono<ResponseEntity<Void>> deleteBeer(@PathVariable("beerId") String beerId) {
        return beerService.getById(beerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(BeerDto::getId)
                .flatMap(beerService::delete)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
