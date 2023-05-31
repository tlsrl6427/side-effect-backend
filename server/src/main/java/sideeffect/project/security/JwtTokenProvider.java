package sideeffect.project.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.UserRoleType;

import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expired}")
    private String expired;

    private final UserDetailsServiceImpl userDetailsService;

    public String getUserName(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("name", String.class);
    }

    public boolean validateAccessToken(String accessToken){
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
            return false;
        }catch (UnsupportedJwtException e){
            throw new AuthException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);
        }catch (MalformedJwtException e){
            throw new AuthException(ErrorCode.ACCESS_TOKEN_MALFORMED);
        }catch (SignatureException e){
            throw new AuthException(ErrorCode.ACCESS_TOKEN_SIGNATURE_FAILED);
        }catch (ExpiredJwtException e){
            throw new AuthException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }catch (IllegalStateException e){
            throw new AuthException(ErrorCode.ACCESS_TOKEN_ILLEGAL_STATE);
        }
    }

    public String createAccessToken(Authentication authentication){
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        //access token
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(now + 1000 * 60 * 30))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createAccessToken2(String email, UserRoleType userRoleType){
        //권한 가져오기
        String authorities = userRoleType.name();

        long now = System.currentTimeMillis();

        //access token
        return Jwts.builder()
                .setSubject(email)
                .claim("auth", authorities)
                .setExpiration(new Date(now + 1000 * 60 * 60 * 24 * 3))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token){
        String name = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        ProviderType providerType = ProviderType.valueOf((String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("providerType"));
        UserDetails userDetails = userDetailsService.loadUserByUsernameAndProviderType(name, providerType);

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
