package com.example.codexlibris;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("/books")
    Call<List<Book>> getBooks();

}

