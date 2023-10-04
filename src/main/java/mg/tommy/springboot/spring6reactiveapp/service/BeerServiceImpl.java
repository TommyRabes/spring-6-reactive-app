package mg.tommy.springboot.spring6reactiveapp.service;

import lombok.RequiredArgsConstructor;
import mg.tommy.springboot.spring6reactiveapp.mapper.BeerMapper;
import mg.tommy.springboot.spring6reactiveapp.model.domain.Beer;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.repository.BeerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper mapper;
    @Override
    public Flux<BeerDto> listBeers() {
        return beerRepository.findAll()
                .map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> getById(Integer id) {
        return beerRepository.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> save(BeerDto beerDto) {
        return beerRepository.save(mapper.toBeer(beerDto))
                .map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> update(Integer beerId, BeerDto beerDto) {
        return beerRepository.findById(beerId)
                .map(beerToUpdate -> {
                    beerToUpdate.setBeerName(beerDto.getBeerName());
                    beerToUpdate.setBeerStyle(beerDto.getBeerStyle());
                    beerToUpdate.setPrice(beerDto.getPrice());
                    beerToUpdate.setUpc(beerDto.getUpc());
                    beerToUpdate.setQuantityOnHand(beerDto.getQuantityOnHand());
                    return beerToUpdate;
                })
                .flatMap(beerRepository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<BeerDto> patch(Integer beerId, BeerDto beerDto) {
        return beerRepository.findById(beerId)
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
                .flatMap(beerRepository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(Integer beerId) {
        return beerRepository.deleteById(beerId);
    }
}
