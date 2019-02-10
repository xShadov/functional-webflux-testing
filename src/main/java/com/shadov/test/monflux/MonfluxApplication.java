package com.shadov.test.monflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@SpringBootApplication
public class MonfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonfluxApplication.class, args);
	}

	@Bean
	public RouterFunction routerFunction(CustomerFacade customerFacade) {
		return RouterFunctions.route(GET("/customer/read/{name}"), customerFacade::read)
				.andRoute(GET("/customer/add/{name}"), customerFacade::add)
				.andRoute(GET("/customer/process"), customerFacade::process);
	}

}

