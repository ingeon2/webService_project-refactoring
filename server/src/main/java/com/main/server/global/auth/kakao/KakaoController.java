package com.main.server.global.auth.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@Validated
@Transactional
@RequiredArgsConstructor
public class KakaoController { //카카오 오어스 로그인시, 정보 받아올 컨트롤러

    @Value("${spring.kakao.client.clientId}")  // 다른 파일에 저장
    private String clientId;

    @Autowired
    private KakaoMemberService kakaoMemberService;

    @GetMapping("/auth/kakao/callback") //아까 작성한 redirect url
    public String kakaoCallback(String code) throws JsonProcessingException {


        //위의 redirect url 에서 받아오는 해당 코드값과 카카오에서 원하는 정보들을 담아보내면(해당 정보들은 아래에 있음)
        //카카오에서 accessToken을 발급받고, 그 토큰으로 개인정보를 얻어올것이다.

//        이건 http body에 데이터를 전달하는데, 4가지 데이터를 담으라는 것이다.
//        이 때 MIME 타입이 application/x-www-form-urlencoded;charset-utf-8이라는 것은
//        데이터를 key=value 형태로 만들어서 전달하라는 뜻이다.
//        그래서 key=value 형태로 데이터를 만들어서 전달해보자
//
//        grant_type=authorization_code
//
//        client_id=e054053c94984f87d0... (앱 키 - REST API 키)
//
//        redirect_uri=http://localhost:8080/auth/kakao/callback (내가 설정한 카카오 로그인요청 콜백 주소)
//
//        code={지금 알 수 없다. 동적임} (우리가 응답받는 code를 변수로 넣으면 된다.)

        //위의 내용을 아래 코드로 실행.

        RestTemplate rt = new RestTemplate(); //http 요청을 편하게 보내기 위해 사용하는 라이브러리.
        //해당 클래스에서 url, http메소드 등을 헤더에 담아 요청
        // 어려우면 https://minkwon4.tistory.com/178 에 설명 있음.

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        //post 요청을 하려면 데이터를 설명해주는 헤더 함께 보내는것
        //26행 내용 그대로.

        //데이터 담기
        //중복된 키를 허용하고 하나의 키에 여러 값을 저장해야 할 경우 MultiValueMap을 사용하는 것이 적합.
        //반면에 중복된 키를 허용하지 않고 각 키에 대해 하나의 값을 저장하면서 기본적인 맵 기능만을 사용해야 한다면
        //HashMap을 사용하는 것이 더 적절
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        //이제 헤더와 데이터 합치기.
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, httpHeaders);

        //post 방식으로 요청, 토큰 String으로 받기
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token", // https://{요청할 서버 주소}
                HttpMethod.POST, // 요청할 방식
                kakaoTokenRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터(accessToken) 타입
        );


        ObjectMapper objectMapper = new ObjectMapper();

        KakaoToken kakaoToken = objectMapper.readValue(response.getBody(), KakaoToken.class);
        //카카오 로그인시 발급되는 accessToken을 가져와 여기 kakaoToken 객체로 바꾸어주고,
        //해당 객체에서 필요한 필드변수를 다시 카카오쪽으로 보내어 회원 정보를 얻어올 예정.



        //위와 같이, RestTemplate 사용해서 위에서 발급받은 토큰을 보낼 예정.
        RestTemplate rt2 = new RestTemplate();
        rt2.setRequestFactory(new HttpComponentsClientHttpRequestFactory()); //에러 설명해주는 역할
        HttpHeaders httpHeaders2 = new HttpHeaders();
        httpHeaders2.add("Authorization", "Bearer " + kakaoToken.getAccess_token());
        httpHeaders2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(httpHeaders2);

        ResponseEntity<String> response1 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me", //보낼 주소
                HttpMethod.POST, //요청 방식
                kakaoProfileRequest,
                String.class
        );

        kakaoMemberService.saveUser(response1.getBody());


        return response1.getBody();

    }
}

