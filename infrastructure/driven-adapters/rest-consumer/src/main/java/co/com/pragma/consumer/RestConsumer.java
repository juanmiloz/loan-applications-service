package co.com.pragma.consumer;

import co.com.pragma.model.loanapplication.dto.request.UserDTO;
import co.com.pragma.model.loanapplication.gateways.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserClient {

    private final WebClient client;

    @Override
    @CircuitBreaker(name = "getClientByEmail")
    public Mono<UserDTO> getClientByEmail(String email) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users/email/{email}")
                        .build(email)
                )
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.createException().flatMap(Mono::error)
                )
                .bodyToMono(UserDTO.class);
    }

//    @CircuitBreaker(name = "testGet")
//    public Mono<ObjectResponse> testGet() {
//        return client
//                .get()
//                .retrieve()
//                .bodyToMono(ObjectResponse.class);
//    }
//
//    @CircuitBreaker(name = "testPost")
//    public Mono<ObjectResponse> testPost() {
//        ObjectRequest request = ObjectRequest.builder()
//            .val1("exampleval1")
//            .val2("exampleval2")
//            .build();
//        return client
//                .post()
//                .body(Mono.just(request), ObjectRequest.class)
//                .retrieve()
//                .bodyToMono(ObjectResponse.class);
//    }
}
