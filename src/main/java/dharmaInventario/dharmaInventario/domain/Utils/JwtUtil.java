package dharmaInventario.dharmaInventario.domain.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // TODO: Mueve esto a application.properties/env en producción
    private static final String SECRET_KEY = "clave-super-secreta-y-larga-para-jwt-2025-que-tiene-mas-de-32-bytes";

    // Duración del token en ms (ej: 1 hora)
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Genera un token para un username dado
    public String generarToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrae el username (subject) del token
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    // Método genérico para extraer cualquier claim
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todos los claims (y valida la firma)
    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extrae la fecha de expiración
    private Date extraerExpiration(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    // Comprueba si el token está expirado
    private boolean estaExpirado(String token) {
        Date expiration = extraerExpiration(token);
        return expiration.before(new Date());
    }

    // Valida que el token pertenezca al username y no esté expirado
    // Nombre usado en tu filtro: esTokenValido
    public boolean esTokenValido(String token, String username) {
        try {
            final String usernameExtraido = extraerUsername(token);
            return (usernameExtraido != null && usernameExtraido.equals(username) && !estaExpirado(token));
        } catch (Exception e) {
            // Si ocurre cualquier excepción al parsear/validar, se considera inválido
            return false;
        }
    }

    // (Opcional) un alias con otro nombre si lo usas en otra parte
    public boolean validarToken(String token, String username) {
        return esTokenValido(token, username);
    }
}
