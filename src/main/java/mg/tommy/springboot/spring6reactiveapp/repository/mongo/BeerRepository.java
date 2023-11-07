package mg.tommy.springboot.spring6reactiveapp.repository.mongo;

import mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Giving a different bean name is required because there is another bean class having the same name
@Repository("MongoBeerRepository")
public interface BeerRepository extends ReactiveMongoRepository<Beer, String> {

    Mono<Beer> findFirstByBeerName(String beerName);

    Flux<Beer> findByBeerStyle(String beerStyle);

}
