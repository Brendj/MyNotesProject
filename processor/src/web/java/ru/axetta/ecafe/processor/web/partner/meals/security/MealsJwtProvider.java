package ru.axetta.ecafe.processor.web.partner.meals.security;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

@Component
public class MealsJwtProvider {
    private Logger logger = LoggerFactory.getLogger(MealsJwtProvider.class);

    public boolean validateToken(String token) throws MealsInvalidToken {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            verifyToken(token);

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            logger.info("Token claims: " + claims.toString());

            return true;
        } catch (ExpiredJwtException expEx) {
            //log.severe("Token expired");
        } catch (UnsupportedJwtException unsEx) {
           // log.severe("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            //log.severe("Malformed jwt");
        } catch (SignatureException sEx) {
            //log.severe("Invalid signature");
        } catch (Exception e) {
            logger.info("Invalid token");
            throw new MealsInvalidToken("InvalidToken");
        }
        return false;
    }

    private void verifyToken(String token) throws Exception {
        final byte[] keyBytes = RuntimeContext.getAppContext().getBean(AupdPublicKey.class).getKey();
        final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyBytes);
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
        final JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        final SignedJWT signedJWT = SignedJWT.parse(token);
        if (!signedJWT.verify(verifier)) {
            throw new Exception("Signature verification failed");
        }
    }

    public String getMshFromToken(String token) throws MealsInvalidToken {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return (String)claims.getClaim("msh");
        } catch (Exception e) {
            throw new MealsInvalidToken("Msh not found in token");
        }
    }
}
