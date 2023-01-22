package com.hbnu.pojo;

import java.io.Serializable;

/**
 * @Author Tiam
 * @Date 2022/11/2 17:27
 * @Description: 实体类User
 */

public class User implements Serializable {
    private static final long serialVersionUID = -8672896408621876098L;
    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getEmail() {
        return email;
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
