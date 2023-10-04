package mg.tommy.springboot.spring6reactiveapp.mapper;

import mg.tommy.springboot.spring6reactiveapp.model.domain.Beer;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer toBeer(BeerDto dto);
    BeerDto toDto(Beer beer);
}
