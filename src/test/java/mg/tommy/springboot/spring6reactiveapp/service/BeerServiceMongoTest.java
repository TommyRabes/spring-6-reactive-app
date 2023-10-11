package mg.tommy.springboot.spring6reactiveapp.service;

import mg.tommy.springboot.spring6reactiveapp.mapper.BeerMapper;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class BeerServiceMongoTest {

    @Autowired
    @Qualifier("beerServiceMongo")
    BeerService beerService;

    @Autowired
    BeerMapper mapper;

    BeerDto beerDto;
    @BeforeEach
    void setUp() {
        beerDto = mapper.toDto(getTestBeer());
    }

    @Test
    void getByIdTest() {
        BeerDto savedBeer = saveBeer();
        AtomicReference<BeerDto> atomicBeer = new AtomicReference<>();
        beerService.getById(savedBeer.getId())
                .subscribe(atomicBeer::set);
        await().until(() -> atomicBeer.get() != null);

        assertThat(atomicBeer.get().getId()).isEqualTo(savedBeer.getId());
    }

    @Test
    void getByBeerNameTest() {
        BeerDto savedBeer = saveBeer();
        BeerDto foundDto = beerService.getByBeerName(savedBeer.getBeerName()).block();
        assertThat(foundDto.getBeerName()).isEqualTo(savedBeer.getBeerName());
    }

    @Test
    void getByBeerStyleTest() {
        BeerDto savedBeer = saveBeer();
        AtomicReference<List<BeerDto>> atomicList = new AtomicReference<>();
        beerService.getByBeerStyle(savedBeer.getBeerStyle())
                .collectList()
                .subscribe(atomicList::set);

        await().until(() -> atomicList.get() != null);
        assertThat(atomicList.get()).isNotEmpty();
        assertThat(atomicList.get()).allSatisfy(dto -> {
            assertThat(dto.getBeerStyle()).isEqualTo(savedBeer.getBeerStyle());
        });
    }

    @Test
    void saveBeerTest() {
        AtomicReference<BeerDto> atomicBeer = new AtomicReference<>();
        Mono<BeerDto> savedBeer = beerService.save(beerDto);
        savedBeer.subscribe(atomicBeer::set);
        await().until(() -> atomicBeer.get() != null);

        assertThat(atomicBeer.get().getId()).isNotEmpty();
    }

    @Test
    void updateBeerTest() {
        BeerDto savedBeer = saveBeer();
        BeerDto updatedBeer = savedBeer.toBuilder()
                .beerName("Strong Space Dust")
                .build();
        AtomicReference<BeerDto> atomicBeer = new AtomicReference<>();
        beerService.update(savedBeer.getId(), updatedBeer)
                .subscribe(atomicBeer::set);
        await().until(() -> atomicBeer.get() != null);

        assertThat(atomicBeer.get().getId()).isEqualTo(savedBeer.getId());
        assertThat(atomicBeer.get().getBeerName()).isEqualTo(updatedBeer.getBeerName());
    }

    @Test
    void deleteBeerTest() {
        BeerDto savedBeer = saveBeer();
        beerService.delete(savedBeer.getId()).block();

        Mono<BeerDto> emptyMono = beerService.getById(savedBeer.getId());
        StepVerifier.create(emptyMono).expectNextCount(0).verifyComplete();
    }

    private BeerDto saveBeer() {
        return beerService.save(beerDto).block();
    }


    public static Beer getTestBeer() {
        return Beer.builder()
                .beerName("Space Dust")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("14531")
                .build();
    }
}