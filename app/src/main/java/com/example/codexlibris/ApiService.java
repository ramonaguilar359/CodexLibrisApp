package com.example.codexlibris;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}

