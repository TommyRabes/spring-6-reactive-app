package mg.tommy.springboot.spring6reactiveapp.repository.mongo;

import mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository("MongoCustomerRepository")
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
