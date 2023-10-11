package mg.tommy.springboot.spring6reactiveapp.web.fn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BeerRouter {
    public static final String BASE_PATH = "/api/v3/beers";
    public static final String BEER_PATH_ID = BASE_PATH + "/{beerId}";

    @Bean
    public RouterFunction<ServerResponse> beerRoutes(BeerHandler handler) {
        return route()
                .GET(BASE_PATH, accept(APPLICATION_JSON), handler::listBeers)
                .GET(BEER_PATH_ID, handler::getById)
                .POST(BASE_PATH, accept(APPLICATION_JSON), handler::save)
                .PUT(BEER_PATH_ID, accept(APPLICATION_JSON), handler::update)
                .PATCH(BEER_PATH_ID, accept(APPLICATION_JSON), handler::patch)
                .DELETE(BEER_PATH_ID, handler::delete)
                .build();
    }
}
