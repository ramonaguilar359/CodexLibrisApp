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

@RunWith(AndroidJUnit4.class)
public class GenereCrudIntegrationTest {

    private ApiService apiService;
    private String token;

    @Before
    public void setup() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
        token = obtenirTokenAdmin();
    }

    @Test
    public void testCrudCompletGenere() throws Exception {
        // 1. Crear gènere
        GenreRequest nouGenere = new GenreRequest("Ciència ficció", "Gènere que inclou elements imaginaris científics o tecnològics.");
        Response<Void> createResponse = apiService.createGenre(token, nouGenere).execute();
        assertTrue("Error en la creació del gènere. Codi: " + createResponse.code(), createResponse.isSuccessful());

        // 2. Consultar gèneres
        Response<List<Genre>> llistaResponse = apiService.getGenres(token).execute();
        assertTrue("Error en la consulta de gèneres. Codi: " + llistaResponse.code(), llistaResponse.isSuccessful());
        List<Genre> generes = llistaResponse.body();
        assertNotNull("La llista de gèneres no hauria de ser nul·la", generes);
        assertFalse("La llista de gèneres no hauria d'estar buida", generes.isEmpty());

        Genre genereCreat = generes.get(generes.size() - 1);
        int genreId = genereCreat.getId();
        assertEquals("El nom del gènere no coincideix", "Ciència ficció", genereCreat.getName());

        // 3. Actualitzar
        GenreRequest genereActualitzat = new GenreRequest("Ciència especulativa", "Gènere de reflexió tecnològica");
        Response<Void> updateResponse = apiService.updateGenre(token, genreId, genereActualitzat).execute();
        assertTrue("Error en l'actualització del gènere. Codi: " + updateResponse.code(), updateResponse.isSuccessful());

        // 4. Verificar canvis
        Response<Genre> genereDetalls = apiService.getGenreById(token, genreId).execute();
        assertTrue("Error en la consulta per ID. Codi: " + genereDetalls.code(), genereDetalls.isSuccessful());
        assertEquals("Nom incorrecte", "Ciència especulativa", genereDetalls.body().getName());

        // 5. Eliminar
        Response<Void> deleteResponse = apiService.deleteGenre(token, genreId).execute();
        assertTrue("Error en l'eliminació del gènere. Codi: " + deleteResponse.code(), deleteResponse.isSuccessful());

        // 6. Verificar eliminació
        Response<Genre> genereEliminat = apiService.getGenreById(token, genreId).execute();
        assertFalse("El gènere eliminat encara existeix", genereEliminat.isSuccessful());
        assertEquals("S'esperava un 404 per gènere eliminat", 404, genereEliminat.code());
    }

    private String obtenirTokenAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin");
        Response<LoginResponse> response = apiService.login(loginRequest).execute();
        assertTrue("Login fallit. Codi: " + response.code(), response.isSuccessful());
        String token = response.body().getToken();
        assertNotNull("Token nul", token);
        return "Bearer " + token;
    }
}