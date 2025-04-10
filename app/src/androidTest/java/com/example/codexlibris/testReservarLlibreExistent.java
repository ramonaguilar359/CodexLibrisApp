package com.example.codexlibris;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Response;

import static org.junit.Assert.*;

/**
 * Proves d'integració relacionades amb la reserva de llibres.
 * Es comprova el comportament del backend davant peticions amb i sense autenticació.
 */
@RunWith(AndroidJUnit4.class)
public class testReservarLlibreExistent {

    private ApiService apiService;

    /**
     * Inicialitza el client Retrofit abans de cada prova.
     */
    @Before
    public void setup() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    /**
     * Simula la reserva d’un llibre existent des del rol d’administrador.
     * La reserva consisteix a marcar el llibre com no disponible.
     */
    @Test
    public void testReservarLlibreExistent() throws Exception {
        String token = obtenirTokenAdmin();

        Author autor = new Author();
        autor.setId(1);
        autor.setName("Gabriel García Márquez");
        autor.setBirth_date("1927-03-06");
        autor.setNationality("Colòmbia");
        autor.setCreated_at("2025-04-09T13:50:53.619954");
        autor.setUpdated_at("2025-04-09T13:50:53.619954");

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Novel·la");
        genre.setDescription("Narracio en prosa de ficcio");
        genre.setCreated_at("2025-04-09T13:50:53.624596");
        genre.setUpdated_at("2025-04-09T13:50:53.624596");

        Book llibre = new Book();
        llibre.setId(1);
        llibre.setTitle("La casa de la mar");
        llibre.setIsbn("978-84-123456-01");
        llibre.setPublished_date("2020-05-14T00:00:00");
        llibre.setAvailable(false);
        llibre.setAuthor(autor);
        llibre.setGenre(genre);
        llibre.setCreated_at("2025-04-09T13:50:53.631134");
        llibre.setUpdated_at("2025-04-09T13:50:53.631134");

        Response<Void> response = apiService.updateBook(token, llibre.getId(), llibre).execute();

        if (!response.isSuccessful()) {
            System.out.println("Error reservant llibre:");
            System.out.println("Codi de resposta: " + response.code());
            if (response.errorBody() != null) {
                System.out.println("Cos de l'error: " + response.errorBody().string());
            }
        }

        assertTrue("La reserva hauria de ser exitosa", response.isSuccessful());
    }

    /**
     * Comprova que no es pot reservar un llibre si no s’envia el token d’autenticació.
     */
    @Test
    public void testReservaSenseTokenHaDeFallar() throws Exception {
        Author autor = new Author();
        autor.setId(1);
        autor.setName("Gabriel García Márquez");
        autor.setBirth_date("1927-03-06");
        autor.setNationality("Colòmbia");
        autor.setCreated_at("2025-04-09T13:50:53.619954");
        autor.setUpdated_at("2025-04-09T13:50:53.619954");

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Novel·la");
        genre.setDescription("Narracio en prosa de ficcio");
        genre.setCreated_at("2025-04-09T13:50:53.624596");
        genre.setUpdated_at("2025-04-09T13:50:53.624596");

        Book llibre = new Book();
        llibre.setId(1);
        llibre.setTitle("La casa de la mar");
        llibre.setIsbn("978-84-123456-01");
        llibre.setPublished_date("2020-05-14T00:00:00");
        llibre.setAvailable(false);
        llibre.setAuthor(autor);
        llibre.setGenre(genre);
        llibre.setCreated_at("2025-04-09T13:50:53.631134");
        llibre.setUpdated_at("2025-04-09T13:50:53.631134");

        Response<Void> response = apiService.updateBook(null, llibre.getId(), llibre).execute();

        assertFalse("La petició hauria de fallar per manca de token", response.isSuccessful());
        assertEquals("El codi hauria de ser 401 Unauthorized", 401, response.code());
    }

    /**
     * Fa login amb l'usuari administrador i retorna el token d'accés.
     */
    private String obtenirTokenAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin");
        Response<LoginResponse> response = apiService.login(loginRequest).execute();
        assertTrue("El login hauria de ser correcte", response.isSuccessful());

        String token = response.body().getToken();
        assertNotNull("El token no hauria de ser nul", token);
        return "Bearer " + token;
    }
}
