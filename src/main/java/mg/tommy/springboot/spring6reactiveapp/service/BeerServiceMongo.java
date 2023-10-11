package mg.tommy.springboot.spring6reactiveapp.service;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.mapper.BeerMapper;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer;
import mg.tommy.springboot.spring6reactiveapp.repository.mongo.BeerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BeerServiceMongo implements BeerService {
    private final BeerMapper mapper;
    private final BeerRepository repository;

    @Override
    public Flux<BeerDto> listBeers() {
        return repository.findAll().map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> getById(String id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> getByBeerName(String beerName) {
        return repository.findFirstByBeerName(beerName).map(mapper::toDto);
    }

    @Override
    public Flux<BeerDto> getByBeerStyle(String beerStyle) {
        return repository.findByBeerStyle(beerStyle).map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> save(BeerDto beerDto) {
        return repository.save(mapper.toNoSqlBeer(beerDto))
                .map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> update(String beerId, BeerDto beerDto) {
        return repository.findById(beerId)
                .map(beerToUpdate -> {
                    beerToUpdate.setBeerName(beerDto.getBeerName());
                    beerToUpdate.setBeerStyle(beerDto.getBeerStyle());
                    beerToUpdate.setPrice(beerDto.getPrice());
                    beerToUpdate.setUpc(beerDto.getUpc());
                    beerToUpdate.setQuantityOnHand(beerDto.getQuantityOnHand());
                    return beerToUpdate;
                })
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> patch(String beerId, BeerDto beerDto) {
        return repository.findById(beerId)
                .map(beerToPatch -> {
                    Beer.BeerBuilder builder = beerToPatch.toBuilder();
                    if (StringUtils.hasText(beerDto.getBeerName())) {
                        builder.beerName(beerDto.getBeerName());
                    }
                    if (StringUtils.hasText(beerDto.getBeerStyle())) {
                        builder.beerStyle(beerDto.getBeerStyle());
                    }
                    if (Objects.nonNull(beerDto.getPrice())) {
                        builder.price(beerDto.getPrice());
                    }
                    if (StringUtils.hasText(beerDto.getUpc())) {
                        builder.upc(beerDto.getUpc());
                    }
                    if (Objects.nonNull(beerDto.getQuantityOnHand())) {
                        builder.quantityOnHand(beerDto.getQuantityOnHand());
                    }
                    return builder.build();
                })
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(String beerId) {
        return repository.deleteById(beerId);
    }
}
