package mg.tommy.springboot.spring6reactiveapp.web.fn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CustomerRouter {
    protected static final String BASE_PATH = "/api/v3/customers";
    protected static final String CUSTOMER_PATH_ID = BASE_PATH + "/{customerId}";

    @Bean
    public RouterFunction<ServerResponse> customerRoute(CustomerHandler handler) {
        return route()
                .GET(BASE_PATH, handler::listCustomers)
                .GET(CUSTOMER_PATH_ID, handler::getById)
                .POST(BASE_PATH, accept(APPLICATION_JSON), handler::save)
                .PUT(CUSTOMER_PATH_ID, accept(APPLICATION_JSON), handler::update)
                .PATCH(CUSTOMER_PATH_ID, accept(APPLICATION_JSON), handler::patch)
                .DELETE(CUSTOMER_PATH_ID, handler::delete)
                .build();
    }
}
