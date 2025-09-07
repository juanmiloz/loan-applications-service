package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.dto.request.UserDTO;
import reactor.core.publisher.Mono;

public interface UserClient {

    Mono<UserDTO> getClientByEmail(String email);

}
