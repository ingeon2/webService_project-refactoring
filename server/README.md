# 프로젝트 후 코드 리팩터링

## 5/26  
프로젝트 마무리. 이후 코드 리팩터링 + 코드리뷰는 혼자해보기 (어디든지 상관 없음)  
  
처음 리팩터링  
### class org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser cannot be cast to  
### class com.main.server.auth.oauth.CustomOAuth2User 에러임.  
즉,  
내가 DefaultOAuth2User를 상속받도록 만든 CustomOAuth2User 클래스가 있음.  
근데 오어스 로그인 성공해서 받아온 principal(authentication.getPrincipal();)을  
도무지  DefaultOAuth2User 클래스로 보지 않고  
DefaultOidcUser 클래스로 봐서 (CustomOAuth2User) authentication.getPrincipal(); 에서 에러가 나온다는 내용임.  

