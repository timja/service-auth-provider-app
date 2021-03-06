package uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers;

import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenExpiredException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenSignatureException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.UnmappedTokenException;

import java.security.Key;

@Slf4j
public abstract class JwtVerifier {

    public static String verifyAndExtractSubject(String jwt, Clock clock, String signatureVerificationKey) {
        return verifyAndExtractSubject(jwt, Jwts.parser().setClock(clock).setSigningKey(signatureVerificationKey));
    }

    public static String verifyAndExtractSubject(String jwt, Clock clock, Key signatureVerificationKey) {
        return verifyAndExtractSubject(jwt, Jwts.parser().setClock(clock).setSigningKey(signatureVerificationKey));
    }

    private static String verifyAndExtractSubject(String jwt, JwtParser jwtParser) {
        try {
            return jwtParser
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();

        } catch (SignatureException e) {
            log.warn("Invalid token signature", e);
            throw new TokenSignatureException(e);
        } catch (ExpiredJwtException e) {
            log.info("Token expired", e);
            throw new TokenExpiredException(e);
        } catch (JwtException e) {
            log.error("Unmapped token exception", e);
            throw new UnmappedTokenException(e);
        }
    }
}
