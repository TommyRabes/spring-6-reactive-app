package mg.tommy.springboot.spring6reactiveapp.mapper;

import mg.tommy.springboot.spring6reactiveapp.model.entity.h2.Beer;
import mg.tommy.springboot.spring6reactiveapp.model.dto.BeerDto;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer toSqlBeer(BeerDto dto);
    BeerDto toDto(Beer beer);
    mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer toNoSqlBeer(BeerDto dto);
    BeerDto toDto(mg.tommy.springboot.spring6reactiveapp.model.entity.mongo.Beer beer);
}
