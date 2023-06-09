package com.main.server.auth.kakaooauth;

import com.google.gson.Gson;
import org.joda.time.DateTime;

public class KakaoMember {
    //카카오에서 얻어오는 정보로 kakaoMember으로 만들고, kakaoMember을 Member로 만들어서 저장하기 위해 만든 클래스
    private String id;
    private DateTime connected_at;
    private Gson properties;
    private Gson kakao_account;
}
