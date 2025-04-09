package com.example.codexlibris;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Response;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class  testReservarLlibreExistent {

    private ApiService apiService;

    @Before
    public void setup() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @Test
    public void testReservarLlibreExistent() throws Exception {

        String token = obtenirTokenAdmin();

        // També cal posar-hi author i genre si el backend els espera
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
        llibre.setAvailable(false); // ← Aquí fem la reserva
        llibre.setAuthor(autor);
        llibre.setGenre(genre);
        llibre.setCreated_at("2025-04-09T13:50:53.631134");
        llibre.setUpdated_at("2025-04-09T13:50:53.631134");


        // Crida al backend per reservar (editar)
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

    @Test
    public void testReservaSenseTokenHaDeFallar() throws Exception {
        // Preparem el llibre complet (com en el test anterior)
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
        llibre.setAvailable(false); // ← Intentem reservar
        llibre.setAuthor(autor);
        llibre.setGenre(genre);
        llibre.setCreated_at("2025-04-09T13:50:53.631134");
        llibre.setUpdated_at("2025-04-09T13:50:53.631134");

        // Crida sense token
        Response<Void> response = apiService.updateBook(null, llibre.getId(), llibre).execute();

        // Comprovem que la resposta és un error d'autenticació
        assertFalse("La petició hauria de fallar per manca de token", response.isSuccessful());
        assertEquals("El codi hauria de ser 401 Unauthorized", 401, response.code());
    }


    private String obtenirTokenAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin");
        Response<LoginResponse> response = apiService.login(loginRequest).execute();
        assertTrue("El login hauria de ser correcte", response.isSuccessful());

        String token = response.body().getToken();
        assertNotNull("El token no hauria de ser nul", token);
        return "Bearer " + token;
    }
}