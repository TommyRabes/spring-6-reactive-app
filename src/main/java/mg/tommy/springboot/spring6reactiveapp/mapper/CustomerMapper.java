package mg.tommy.springboot.spring6reactiveapp.mapper;

import mg.tommy.springboot.spring6reactiveapp.model.domain.Customer;
import mg.tommy.springboot.spring6reactiveapp.model.dto.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    CustomerDto toDto(Customer customer);
    Customer toCustomer(CustomerDto dto);
}
