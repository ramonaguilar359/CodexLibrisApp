package com.example.codexlibris;

import java.util.List;

import retrofit2.http.DELETE;
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
    Call<Book> getBookById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @POST("/books")
    Call<Void> createBook(
            @Header("Authorization") String token,
            @Body BookRequest request
    );

    @GET("/authors")
    Call<List<Author>> getAuthors(@Header("Authorization") String token);

    @GET("/genres")
    Call<List<Genre>> getGenres(@Header("Authorization") String token);

    @PUT("/books/{id}")
    Call<Void> updateBook(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Book book
    );

    @DELETE("/books/{id}")
    Call<Void> deleteBook(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    /* @POST("/loans")
    Call<Void> reserveBook(
            @Header("Authorization") String token,
            @Body LoanRequest request
    ); */
}

