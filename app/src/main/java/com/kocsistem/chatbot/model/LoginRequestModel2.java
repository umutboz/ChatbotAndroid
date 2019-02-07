package com.kocsistem.chatbot.model;

/**
 * Created by gurkankesgin on 20.6.2017 .
 */

public class LoginRequestModel2 {
    private String UserName;
    private String Password;

    public LoginRequestModel2() {
    }

    public LoginRequestModel2(String userName, String password) {
        UserName = userName;
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
