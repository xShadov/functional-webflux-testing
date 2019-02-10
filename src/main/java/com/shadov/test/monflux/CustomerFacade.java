package com.shadov.test.monflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.SplittableRandom;

@Component
public class CustomerFacade {

	@Autowired
	private CustomerRepository customerRepository;

	public Mono<ServerResponse> read(ServerRequest request) {
		return customerRepository.findByName(request.pathVariable("name"))
				.transform(response -> ServerResponse.ok().body(response, Customer.class))
				.or(ServerResponse.badRequest().build());
	}

	public Mono<ServerResponse> add(ServerRequest request) {
		final Customer customer = namedCustomer(request.pathVariable("name"));

		return customerRepository.insert(customer)
				.transform(response -> ServerResponse.ok().body(response, Customer.class))
				.or(ServerResponse.badRequest().build());
	}

	public Mono<ServerResponse> process(ServerRequest request) {
		final Flux<Customer> responses = customerRepository.findAll()
				.flatMap(customer -> customerRepository.insert(namedCustomer(String.format("%s!", customer.getName())))
						.delayElement(Duration.ofMillis(Math.random() < 0.5 ? 2000 : 100))
				);

		return ServerResponse.ok()
				.contentType(MediaType.TEXT_EVENT_STREAM)
				.body(BodyInserters.fromPublisher(responses, Customer.class))
				.or(ServerResponse.badRequest().build());
	}

	private Customer namedCustomer(String name) {
		final Customer customer = new Customer();
		customer.setName(name);
		customer.setAge(new SplittableRandom().nextInt(30));
		return customer;
	}
}
