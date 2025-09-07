package co.com.pragma.security;

import co.com.pragma.model.shared.gateway.AuthGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SecurityAuthGateway implements AuthGateway {

    @Override
    public Mono<String> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(auth -> log.debug("Auth in context: type={}, name={}, authenticated={}, authorities={}",
                        auth != null ? auth.getClass().getSimpleName() : "null",
                        auth != null ? auth.getName() : "null",
                        auth != null && auth.isAuthenticated(),
                        auth != null ? auth.getAuthorities() : "null"))
                .filter(auth -> auth != null && auth.isAuthenticated())
                .map(Authentication::getName)
                .doOnNext(user -> log.info("Resolved current user id: {}", user))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No authenticated principal found in ReactiveSecurityContext");
                    return Mono.empty();
                }));
    }

}
