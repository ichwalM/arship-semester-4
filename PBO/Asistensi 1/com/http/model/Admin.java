package com.http.model;

public class Admin extends Account{
    public Admin(String name, String username, String password) {
        super(name, username, password);
        role = "admin";
    }
}
