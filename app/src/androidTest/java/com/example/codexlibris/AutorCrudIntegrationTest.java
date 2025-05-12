package com.example.codexlibris;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Response;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Proves d'integració completes del CRUD d'autors amb asserts millorats.
 */
@RunWith(AndroidJUnit4.class)
public class AutorCrudIntegrationTest {

    private ApiService apiService;
    private String token;

    @Before
    public void setup() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
        token = obtenirTokenAdmin();
    }

    @Test
    public void testCrudCompletAutor() throws Exception {
        // 1. Crear autor
        AuthorRequest nouAutor = new AuthorRequest("Autor de prova", "1980-01-01", "Catalunya");
        Response<Void> createResponse = apiService.createAuthor(token, nouAutor).execute();
        assertTrue("Error en la creació de l'autor. Codi: " + createResponse.code(), createResponse.isSuccessful());

        // 2. Consultar tots els autors i obtenir l'últim
        Response<List<Author>> llistaResponse = apiService.getAuthors(token).execute();
        assertTrue("Consulta d'autors fallida. Codi: " + llistaResponse.code(), llistaResponse.isSuccessful());

        List<Author> autors = llistaResponse.body();
        assertNotNull("La llista d'autors no hauria de ser nul·la", autors);
        assertFalse("La llista d'autors hauria de contenir almenys un autor", autors.isEmpty());

        Author autorCreat = autors.get(autors.size() - 1);
        int authorId = autorCreat.getId();
        assertEquals("El nom de l'autor no coincideix", "Autor de prova", autorCreat.getName());

        // 3. Actualitzar l'autor
        Author autorModificat = new Author();
        autorModificat.setId(authorId);
        autorModificat.setName("Autor modificat");
        autorModificat.setBirth_date("1981-01-01");
        autorModificat.setNationality("València");

        Response<Void> updateResponse = apiService.updateAuthor(token, authorId, autorModificat).execute();
        assertTrue("Actualització fallida. Codi: " + updateResponse.code(), updateResponse.isSuccessful());

        // 4. Consultar i verificar canvis
        Response<Author> autorDetalls = apiService.getAuthorById(token, authorId).execute();
        assertTrue("Consulta per ID fallida. Codi: " + autorDetalls.code(), autorDetalls.isSuccessful());
        assertNotNull("El cos de la resposta de l'autor és nul", autorDetalls.body());

        Author autorActualitzat = autorDetalls.body();
        assertEquals("Nom incorrecte després de l'actualització", "Autor modificat", autorActualitzat.getName());
        assertEquals("Nacionalitat incorrecta", "València", autorActualitzat.getNationality());
        assertEquals("Data de naixement incorrecta", "1981-01-01", autorActualitzat.getBirth_date());

        // 5. Eliminar l'autor
        Response<Void> deleteResponse = apiService.deleteAuthor(token, authorId).execute();
        assertTrue("Error al eliminar l'autor. Codi: " + deleteResponse.code(), deleteResponse.isSuccessful());

        // 6. Comprovar que l'autor ja no existeix
        Response<Author> autorEliminat = apiService.getAuthorById(token, authorId).execute();
        assertFalse("L'autor eliminat encara existeix", autorEliminat.isSuccessful());
        assertEquals("S'esperava un 404 al consultar un autor eliminat", 404, autorEliminat.code());
    }

    /**
     * Fa login amb l'usuari administrador i retorna el token d'accés.
     */
    private String obtenirTokenAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin");
        Response<LoginResponse> response = apiService.login(loginRequest).execute();
        assertTrue("Login fallit. Codi: " + response.code(), response.isSuccessful());

        String token = response.body().getToken();
        assertNotNull("Token nul rebut després del login", token);
        return "Bearer " + token;
    }
}