package mg.tommy.springboot.spring6reactiveapp.repository.h2;

import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository("R2dbcCustomerRepository")
public interface CustomerRepository extends R2dbcRepository<Customer, Integer> {
}
