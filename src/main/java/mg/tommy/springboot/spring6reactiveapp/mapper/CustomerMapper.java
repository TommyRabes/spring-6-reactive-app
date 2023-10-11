package mg.tommy.springboot.spring6reactiveapp.mapper;

import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Customer;
import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    CustomerDto toDto(Customer customer);
    CustomerDto toDto(mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Customer customer);
    Customer toSqlCustomer(CustomerDto dto);
    mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Customer toNoSqlCustomer(CustomerDto dto);
}
