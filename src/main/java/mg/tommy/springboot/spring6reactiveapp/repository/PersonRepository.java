package mg.tommy.springboot.spring6reactiveapp.repository;

import mg.tommy.springboot.spring6reactiveapp.model.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {

    Mono<Person> getById(Integer id);
    Flux<Person> findAll();
}
