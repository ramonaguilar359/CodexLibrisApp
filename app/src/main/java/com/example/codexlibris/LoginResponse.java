package com.example.codexlibris;

public class LoginResponse {
    private String token;
    private String username;
    private int roleId;
    private int id;

    public String getToken() { return token; }

    public int getRoleId() { return roleId; }

    public int getId() { return id; }

    public String getUsername() { return username; }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", roleId=" + roleId +
                '}';
    }
}
