package com.chensan.common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String UserID;
    private String password;
    private String type;//newUser,commonUser

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User(){}

    public User(String userID, String password) {
        UserID = userID;
        this.password = password;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}