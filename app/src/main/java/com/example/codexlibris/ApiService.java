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

    /**
     * Obté un autor pel seu identificador únic.
     *
     * @param token token JWT per autorització
     * @param id identificador de l'autor
     * @return objecte Author si existeix
     */
    @GET("/authors/{id}")
    Call<Author> getAuthorById(@Header("Authorization") String token, @Path("id") int id);

    /**
     * Crea un nou autor amb les dades proporcionades.
     *
     * @param token token JWT per autorització
     * @param authorRequest dades de l'autor a crear
     * @return resposta buida en cas d'èxit
     */
    @POST("authors")
    Call<Void> createAuthor(@Header("Authorization") String token, @Body AuthorRequest authorRequest);

    /**
     * Actualitza les dades d'un autor existent.
     *
     * @param token token JWT per autorització
     * @param id identificador de l'autor a actualitzar
     * @param author objecte Author amb les dades noves
     * @return resposta buida en cas d'èxit
     */
    @PUT("/authors/{id}")
    Call<Void> updateAuthor(@Header("Authorization") String token, @Path("id") int id, @Body Author author);

    /**
     * Elimina un autor existent pel seu ID.
     *
     * @param token token JWT per autorització
     * @param id identificador de l'autor a eliminar
     * @return resposta buida en cas d'èxit
     */
    @DELETE("/authors/{id}")
    Call<Void> deleteAuthor(@Header("Authorization") String token, @Path("id") int id);

    /**
     * Obté la llista de gèneres disponibles.
     *
     * @param token token JWT per autorització
     * @return llista d'objectes Genre
     */
    @GET("genres")
    Call<List<Genre>> getGenres(@Header("Authorization") String token);

    /**
     * Obté un gènere pel seu identificador.
     *
     * @param token token JWT per autorització
     * @param id identificador del gènere
     * @return objecte Genre si existeix
     */
    @GET("genres/{id}")
    Call<Genre> getGenreById(@Header("Authorization") String token, @Path("id") int id);

    /**
     * Crea un nou gènere.
     *
     * @param token token JWT per autorització
     * @param genre objecte amb el nom i descripció del gènere
     * @return resposta buida en cas d'èxit
     */
    @POST("genres")
    Call<Void> createGenre(@Header("Authorization") String token, @Body GenreRequest genre);

    /**
     * Actualitza un gènere existent.
     *
     * @param token token JWT per autorització
     * @param id identificador del gènere a modificar
     * @param genre dades noves del gènere
     * @return resposta buida en cas d'èxit
     */
    @PUT("genres/{id}")
    Call<Void> updateGenre(@Header("Authorization") String token, @Path("id") int id, @Body GenreRequest genre);

    /**
     * Elimina un gènere pel seu ID.
     *
     * @param token token JWT per autorització
     * @param id identificador del gènere a eliminar
     * @return resposta buida en cas d'èxit
     */
    @DELETE("genres/{id}")
    Call<Void> deleteGenre(@Header("Authorization") String token, @Path("id") int id);

    /**
     * Obté la llista d'esdeveniments.
     *
     * @param authHeader capçalera d'autenticació amb el token JWT
     * @return llista d'objectes Event
     */
    @GET("/events")
    Call<List<Event>> getEvents(@Header("Authorization") String authHeader);

    /**
     * Obté un esdeveniment pel seu ID.
     *
     * @param authHeader capçalera d'autenticació amb el token JWT
     * @param id identificador de l'esdeveniment
     * @return objecte Event si existeix
     */
    @GET("/events/{id}")
    Call<Event> getEventById(@Header("Authorization") String authHeader, @Path("id") int id);

    /**
     * Crea un nou esdeveniment.
     *
     * @param authHeader capçalera d'autenticació amb el token JWT
     * @param request objecte amb les dades de l'esdeveniment a crear
     * @return resposta buida en cas d'èxit
     */
    @POST("/events")
    Call<Void> createEvent(@Header("Authorization") String authHeader, @Body EventRequest request);

    /**
     * Actualitza un esdeveniment existent.
     *
     * @param authHeader capçalera d'autenticació amb el token JWT
     * @param id identificador de l'esdeveniment
     * @param request objecte amb les noves dades
     * @return resposta buida en cas d'èxit
     */
    @PUT("/events/{id}")
    Call<Void> updateEvent(@Header("Authorization") String authHeader, @Path("id") int id, @Body Event request);

    /**
     * Elimina un esdeveniment.
     *
     * @param authHeader capçalera d'autenticació amb el token JWT
     * @param id identificador de l'esdeveniment
     * @return resposta buida en cas d'èxit
     */
    @DELETE("/events/{id}")
    Call<Void> deleteEvent(@Header("Authorization") String authHeader, @Path("id") int id);


}


