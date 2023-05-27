# 프로젝트 후 코드 리팩터링

## 5/26  
프로젝트 마무리. 이후 코드 리팩터링 + 코드리뷰는 혼자해보기 (어디든지 상관 없음)<br/><br/>  

## 5/27
처음 리팩터링  
### class org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser cannot be cast to  
### class com.main.server.auth.oauth.CustomOAuth2User 에러임.  
즉,  
내가 DefaultOAuth2User를 상속받도록 만든 CustomOAuth2User 클래스가 있음.  
근데 오어스 로그인 성공해서 받아온 principal(authentication.getPrincipal();)을  
도무지  DefaultOAuth2User 클래스로 보지 않고 DefaultOidcUser 클래스로 보기 때문에  
(CustomOAuth2User) authentication.getPrincipal(); 에서 에러가 나온다는 내용임.  

## 5/28  
둘 클래스 관계가 어떻게될까? 챗 지피티와 구글링을 통해 알아보니,  
서로 다른 클래스이며, 직접적인 상속 관계나 상하위 클래스의 관계는 없고 따라서 두 클래스 중 어느 것이 상위 클래스인지라는 개념은 적용되지 않음.  
  
상하관계는 아니더라도, 그런데 분명히 연관이 있으니 두개를 착각한다고 판단.  
구글 OAuth2에서 정보를 받아올 때, 범위를 지정하지 않아 openid까지 받아옴. 그래서 idToken을 필드로 가지고있는 DefaultOidcUser 클래스로 인식해버렸던것.  
