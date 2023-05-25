package com.main.server.auth.oauth;

import com.main.server.auth.jwt.JwtTokenizer;
import com.main.server.auth.mail.MailService;
import com.main.server.auth.utils.CustomAuthorityUtils;
import com.main.server.member.entity.Member;
import com.main.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils customAuthorityUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        if(authentication.getPrincipal() instanceof DefaultOAuth2User) {
            log.info("이거 오어스유저 맞는디"); //맞다고 나옴, 근데 왜 캐스팅 못하는거지?
        }

        if(authentication.getPrincipal() instanceof DefaultOidcUser) {
            log.info("이거 오익유저 맞는디"); //맞다고 나옴, 그럼 둘 다의 인스턴스가 되는거네?
        }

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();


        String accessToken = delegateAccessToken(oAuth2User);
        //String refreshToken = delegateRefreshToken(oAuth2User);

        String redirectURI = "http://www.naver.com"; // 서버주소에서 리팩토링 위해 바꿔놓음.
        log.info("## 리다이렉트 -> {}", redirectURI);
        log.info("## 토큰: {}", accessToken);
        getRedirectStrategy().sendRedirect(request, response, createURI(accessToken).toString());

    }

    private URI createURI(String accessToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        //queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                //.scheme("https")
                .host("www.naver.com") // 서버주소에서 리팩토링 위해 바꿔놓음.
                .queryParams(queryParams).build().toUri();
    }

    private String delegateAccessToken(OAuth2User oAuth2User) {
        String email = String.valueOf(oAuth2User.getAttributes().get("email"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", email);
        claims.put("roles", customAuthorityUtils.createRoles(email));

        String subject = email;
        Date expiration = jwtTokenizer.getTokenExpiration(
                jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration,
                base64EncodedSecretKey);

        return accessToken;
    }

//    private String delegateRefreshToken(CustomOAuth2User oAuth2User) {
//        String subject = oAuth2User.getEmail();
//        Date expiration = jwtTokenizer.getTokenExpiration(
//                jwtTokenizer.getRefreshTokenExpirationMinutes());
//        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
//
//        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration,
//                base64EncodedSecretKey);
//
//        return refreshToken;
//    }
}