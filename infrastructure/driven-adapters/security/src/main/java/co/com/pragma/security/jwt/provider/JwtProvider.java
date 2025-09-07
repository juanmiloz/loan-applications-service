package co.com.pragma.security.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Duration expiration;

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return true;
        } catch (ExpiredJwtException e) {
            log.error("token expired");
        }catch (UnsupportedJwtException e) {
            log.error("token unsupported");
        } catch (MalformedJwtException e) {
            log.error("token malformed");
        } catch (SignatureException e) {
            log.error("bad signature");
        } catch (IllegalArgumentException e) {
            log.error("illegal args");
        }
        return false;
    }

    private SecretKey getKey(String secret) {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

}
