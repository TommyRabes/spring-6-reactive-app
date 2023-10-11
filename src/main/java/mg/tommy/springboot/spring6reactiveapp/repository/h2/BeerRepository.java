package mg.tommy.springboot.spring6reactiveapp.repository.h2;

import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Beer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Priorly extends ReactiveCrudRepository, now that we've added the spring-boot-starter-data-mongodb-reactive as a dependency,
// we need to extend the R2dbcRepository, otherwise Spring will not find (or implement) a bean for this interface
// The R2dbcRepository type is probably a hint to Spring
// to avoid ambiguity whether it's a repository bound to a relational or a non-relational database
// The presence of 2 dependencies is creating conflict if the base repository class is not specific enough
@Repository("R2dbcBeerRepository")
public interface BeerRepository extends R2dbcRepository<Beer, Integer> {

    Mono<Beer> findFirstByBeerName(String beerName);

    Flux<Beer> findByBeerStyle(String beerStyle);

}
