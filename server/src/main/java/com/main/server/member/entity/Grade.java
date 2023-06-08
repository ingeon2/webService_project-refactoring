package com.main.server.member.entity;

public enum Grade { //점수에 따른 등급
    GRADE1("4등"),
    GRADE2("3등"),
    GRADE3("2등"),
    GRADE4("1등"),
    GRADE5("VIP");


    private final String value;

    Grade(String value) {
        this.value = value;
    }


}
