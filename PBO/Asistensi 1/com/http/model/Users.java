package com.http.model;

public class Users  extends Account{
    public Users(String name, String username, String password) {
        super(name, username, password);
        role = "user";
    }

}
