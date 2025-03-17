package com.example.codexlibris;

public class LoginResponse {
    private String token;
    private String username;
    private int roleId;

    public String getToken() {
        return token;
    }

    public int getRoleId() { return roleId; }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", roleId=" + roleId +
                '}';
    }
}
