package mg.tommy.springboot.spring6reactiveapp.repository;

import mg.tommy.springboot.spring6reactiveapp.model.domain.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}
