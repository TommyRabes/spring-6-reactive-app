package mg.tommy.springboot.spring6reactiveapp.repository;

import mg.tommy.springboot.spring6reactiveapp.model.domain.Beer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {
}
