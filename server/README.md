# 프로젝트 후 코드 리팩터링

## 2023/5/26  
프로젝트 마무리. 이후 코드 리팩터링 + 코드리뷰는 혼자해보기 (어디든지 상관 없음)<br/><br/>  

## 2023/5/27
처음 리팩터링  (외부 api 오어스 관련)
### class org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser cannot be cast to  
### class com.main.server.auth.googleoauth.CustomOAuth2User 에러임.  
즉,  
내가 DefaultOAuth2User를 상속받도록 만든 CustomOAuth2User 클래스가 있음.  
근데 오어스 로그인 성공해서 받아온 principal(authentication.getPrincipal();)을  
도무지  DefaultOAuth2User 클래스로 보지 않고 DefaultOidcUser 클래스로 보기 때문에  
(CustomOAuth2User) authentication.getPrincipal(); 에서  
캐스팅 할 수 없다는 에러가 나온다는 내용임.<br/><br/>  

## 2023/5/28
### 그럼 에러가 나왔던 이유는 뭐였지?
둘 클래스 관계가 어떻게될까? 챗 지피티와 구글링을 통해 알아보니,  
서로 다른 클래스이며, 직접적인 상속 관계나 상하위 클래스의 관계는 없고 따라서 두 클래스 중 어느 것이 상위 클래스인지라는 개념은 적용되지 않음.  
  
상하관계는 아니더라도, 그런데 분명히 연관이 있으니 두개를 착각한다고 판단.  
구글 OAuth2에서 정보를 받아올 때, 범위를 지정하지 않아 openid까지 받아옴. 그래서 idToken을 필드로 가지고있는 DefaultOidcUser 클래스로 인식해버렸던것.<br/><br/>  
  
## 2023/5/29
### 그래서 어떻게 해결했냐?  
외부 API에서 Member 정보를 받아올때, openId 객체는 빼고 받아와야 한다.  
그렇게 되기 위해,  
#### yml 파일에 가져올때 범위를 지정하는 scope를 달아주었다.  
그 이후로는 더이상 DefaultOidcUser 클래스로 인식하지 않았고,  
DefaultOAuth2User으로 인식할 수 있었다.<br/><br/>  

## 2023/5/31
### 그러고 나서 코드는 어떻게 수정했지?
이전엔 해당 오류때문에 받아온 api를 내 db에 저장하는 일을 해주는 service 클래스를 사용하지 않음.   
즉, SecurityConfiguration 클래스의 filterchain 매서드의 엔드포인트에  
service 클래스를 달지 않고,  
oop 원칙에 어긋나게 성공 핸들러에서 db에 저장까지 실행했지만,  
이제 당당하게 비즈니스로직이 포함된 service클래스를  
filterchain의 endpoint에 달 수 있다.  

## 2023/6/4
### 나 로컬에서 개발할때는, h2 사용했는데, 실제 프로젝트 서버에서는 mysql 사용했다.
### 그 두개의 차이점은?  
(오토에버 질문)

h2 database의 장점
1. 외부 영향없이 독립적 테스트 가능.
2. 원하는 데이터 마음대로 구성 가능.
3. 테스트 후 롤백 불필요.
4. 내부 메모리 db이므로, 빠른 결과 도출 가능.

h2 database의 단점
1. 프로젝트 커질수록 초기화 데이터 양 많아짐.
2. 실제 쿼리 수행과 동일하지 않을 수 있음. (mysql 쿼리 != h2 쿼리)


## 2023/6/5
### 그렇다면, 쿼리문은 어떻게 다를까?
일반적으로, 쿼리문은 거의 같다. 그럼 그나마 차이가 있는 부분은?
<br/><br/>

1. H2에서는 JOIN 문에서 ON 절을 사용하지 않음. 조건은 WHERE 절에 지정해야 함.  

```agsl
mysql
SELECT id
FROM table1
JOIN table2 ON table1.id = table2.id;
```

```agsl
h2
SELECT id
FROM table1
JOIN table2
WHERE table1.id = table2.id;
```  
<br/><br/>

2.H2에서는 쿼리에서 ';'를 생략 가능, mysql은 ';' 사용해야함
```agsl
CREATE TABLE mytable (
  id INT,
  name VARCHAR(255)
);
```
<br/><br/>  

3. 데이터 타입<br/>
   MySQL: DATETIME, TEXT, BLOB 등
   H2: TIMESTAMP, CLOB, BLOB 등
<br/><br/>
4. AUTO_INCREMENT (자동 증가) 설정<br/>
   MySQL: INT 타입 필드에 AUTO_INCREMENT를 지정하여 자동 증가 값 생성
   H2: IDENTITY(1,1)를 사용하여 자동 증가 값 생성
<br/><br/>
5. 문자열 비교<br/>
   MySQL: 문자열 비교에 대소문자를 구분 (CASE SENSITIVE)
   H2: 기본적으로 문자열 비교에 대소문자를 구분하지 않음 (CASE INSENSITIVE)
   <br/><br/>
6. LIMIT 및 OFFSET</br>
   MySQL: LIMIT 및 OFFSET을 사용하여 결과 집합의 행 수를 제한하고 시작 위치를 지정
   H2: LIMIT 및 OFFSET 또는 TOP을 사용하여 결과 집합의 행 수를 제한하고 시작 위치를 지정
   <br/><br/>

## 2023/6/6
### 이에 관해서 찾던중, 핵심을 찾았다!
위에 적어놓은것과 더불어, h2와 mysql은 동적쿼리 작성(||, CONCAT 차이)  
등등에 있어 다른 부분이 많다.
<br/><br/>
### 그럼 쿼리를 어떻게 다 다르게 변환해야하나..?
아니다! 바꿔주는 설정이 존재한다!  
야믈(yml) 설정파일에, spring.datasource.url 경로를 따라 있는  
jdbc:h2:mem:test 의 설정에 ;MODE=MySQL를 붙여주면 된다!  
**즉,**   
```agsl
spring:
  h2:
    console:
      enabled: true
      path: /h2
  datasource:
    url: jdbc:h2:mem:test;MODE=MySQL
```
**이렇게 사용하면 된다!**  

<br/><br/>
### 깨알 꿀팁.  
jdbc:h2:mem:test 얘는 인메모리고,  
jdbc:h2:mem/test 얘는 직접 경로를 따라 들어가보면 db들이 생성된다.  
(앞으로의 테스트에 잘 사용하기.)  

<br/><br/>

## 2023/6/7
그다음, 나는 서버컴퓨터를 업데이트할때는, 서버를 종료시키고  
새롭게 업데이트된 jar 파일을 그냥 실행시켰다. 근데 현직에선 어떻게할까?
오토에버 면접에서 질문받은 문제이다.  

### 여기서 사용하는 개념이, 바로 CD/CI 이다.
### CD/CI는 continuous delivery, continuous integration
### 지속 배포, 지속 통합  
#### 그럼 어떻게 지속적으로 배포하고 통합하고 하냐..?
당연히 내가 안한다. 이 세상엔 이미 개발자들을 위한 개발자들이 너무 많다.  
이렇게 CD/CI를 실행해 줄 수 있는 오픈소스중에, **jenkins**가 있다.  
내가 프로젝트에서 사용했던 AWS의 EC2에서, 업데이트 될때마다 git clone 할 필요 없이,  
알아서  git push 하면 EC2에서 새로운 버전으로 실행까지 마쳐주는 역할이다.  
https://narup.tistory.com/category/Web%20Programming/Jenkins  
https://goddaehee.tistory.com/259?category=399178  
에 들어가보면, 처음부터 자세하게 설명되어있다.  

### 그럼 애플리케이션 실행중에 서버 중단되지 않느냐?
그건, 클라우드 컴퓨터에서 실행 중인 애플리케이션을 중단하지 않고 업데이트하는 방법은 여러가지.  
가장 일반적인 방법 중 하나는 로드 밸런서를 사용하는 것.  
로드 밸런서는 여러 개의 서버에 트래픽을 분산시키는 장치.  
애플리케이션을 업데이트할 때는 로드 밸런서에서 업데이트되지 않은 서버의 트래픽을 제거하고  
업데이트된 서버로 트래픽을 전달. 이렇게 하면 애플리케이션을 중단하지 않고 업데이트할 수 있음.  
그리고, 로드밸런서의 한 방법으로 라운드 로빈이 있다. 이러면 더 이해 잘 가겠지?  
<br/><br/>

## 2023/6/8
### 오늘은, 기능적인 측면에서 로직을 추가해줬다.
#### Member가, 작성한 댓글과 게시글에 따라 차등 등급을 부여하도록
#### Member의 필드에 Point와 Grade라는 필드를 추가해줬다.
점수를 createBoard, createComment 당시에 추가해주려고 했지만,  
nullpoinException 으로 미궁에 빠지고 만다..  
해결은 https://velog.io/@dlsrjsdl6505/%EB%A6%AC%ED%8C%A9%ED%84%B0%EB%A7%81-%EA%B8%B0%EB%8A%A5%EC%B6%94%EA%B0%80 에!!
<br/><br/>

## 2023/6/9
### 오늘은, 카카오 오어스를 추가하기 위해 코드를 수정하고, 추가해주었다.
auth 패키지에 kakao 패키지를 추가하고,  
해당 패키지 안에 요청 보내고 받아올 컨트롤러를 추가해줬다.  
컨트롤러를 통해 json으로 받아오기까지 구현.
https://velog.io/@dlsrjsdl6505/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81-%EC%B9%B4%EC%B9%B4%EC%98%A4-Oauth2-1
<br/><br/>

## 2023/6/9
### 카카오 API를 가져와서 직접 사용하기 위해, 정보를 가공했다.
json으로 받아온 정보를 kakaoMemberService 클래스에서 가공하고,  
kakaoController에서 저장까지 해주는 로직을 완료했다.  
자세한 내용은
https://velog.io/@dlsrjsdl6505/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81-%EC%B9%B4%EC%B9%B4%EC%98%A4-Oauth2-2  
여기서!


