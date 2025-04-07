package com.example.codexlibris;

import java.util.List;

import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("/books")
    Call<List<Book>> getBooks(@Header("Authorization") String authHeader);

    @GET("/books/{id}")
    Call<Book> getBookById(@Path("id") int id);

    @POST("/books")
    Call<Book> createBook(@Body Book book);

    @PUT("/books/{id}")
    Call<Book> updateBook(@Path("id") int id, @Body Book book);
}

