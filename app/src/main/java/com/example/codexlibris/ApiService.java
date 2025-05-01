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

/**
 * Servei d'accés a l'API REST de l'aplicació CodexLibris.
 * Conté les operacions disponibles per autenticar-se i gestionar llibres, autors i gèneres.
 */
public interface ApiService {

    @Headers("Content-Type: application/json")

    /**
     * Inicia sessió amb un usuari i retorna el token JWT si les credencials són vàlides.
     *
     * @param request objecte amb usuari i contrasenya
     * @return resposta amb el token d'autenticació
     */
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * Obté la llista completa de llibres disponibles.
     *
     * @param authHeader capçalera d'autenticació amb el token JWT
     * @return llista de llibres
     */
    @GET("/books")
    Call<List<Book>> getBooks(@Header("Authorization") String authHeader);

    /**
     * Obté els detalls d'un llibre concret pel seu identificador.
     *
     * @param token token JWT per autorització
     * @param id identificador del llibre
     * @return llibre corresponent a l'identificador
     */
    @GET("/books/{id}")
    Call<Book> getBookById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    /**
     * Crea un llibre nou amb les dades proporcionades.
     *
     * @param token token JWT per autorització
     * @param request dades del llibre a crear
     * @return resposta buida en cas d'èxit
     */
    @POST("/books")
    Call<Void> createBook(
            @Header("Authorization") String token,
            @Body BookRequest request
    );

    /**
     * Obté la llista d'autors registrats al sistema.
     *
     * @param token token JWT per autorització
     * @return llista d'autors
     */
    @GET("/authors")
    Call<List<Author>> getAuthors(@Header("Authorization") String token);

    /**
     * Obté la llista de gèneres disponibles.
     *
     * @param token token JWT per autorització
     * @return llista de gèneres
     */
    @GET("/genres")
    Call<List<Genre>> getGenres(@Header("Authorization") String token);

    /**
     * Actualitza les dades d'un llibre existent.
     *
     * @param token token JWT per autorització
     * @param id identificador del llibre a actualitzar
     * @param book llibre amb les dades noves
     * @return resposta buida en cas d'èxit
     */
    @PUT("/books/{id}")
    Call<Void> updateBook(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Book book
    );

    /**
     * Elimina un llibre del sistema.
     *
     * @param token token JWT per autorització
     * @param id identificador del llibre a eliminar
     * @return resposta buida en cas d'èxit
     */
    @DELETE("/books/{id}")
    Call<Void> deleteBook(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @GET("/authors/{id}")
    Call<Author> getAuthorById(@Header("Authorization") String token, @Path("id") int id);

    @POST("authors")
    Call<Void> createAuthor(@Header("Authorization") String token, @Body AuthorRequest authorRequest);

    @PUT("/authors/{id}")
    Call<Void> updateAuthor(@Header("Authorization") String token, @Path("id") int id, @Body Author author);

    @DELETE("/authors/{id}")
    Call<Void> deleteAuthor(@Header("Authorization") String token, @Path("id") int id);





    /*
    // Endpoint pendent d'implementar per gestionar reserves

    @POST("/loans")
    Call<Void> reserveBook(
            @Header("Authorization") String token,
            @Body LoanRequest request
    );
    */
}


