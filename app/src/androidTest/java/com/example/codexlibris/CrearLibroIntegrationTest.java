package com.example.codexlibris;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Response;

import static org.junit.Assert.*;

import android.content.Context;

/**
 * Proves d'integració relacionades amb la creació de llibres.
 * S'utilitza una API REST protegida amb autenticació JWT.
 */
@RunWith(AndroidJUnit4.class)
public class CrearLibroIntegrationTest {

    private Context context;

    private ApiService apiService;

    /**
     * Inicialitza el client Retrofit abans de cada prova.
     */
    @Before
    public void setup() {
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
    }

    /**
     * Comprova que un llibre vàlid es pot crear correctament.
     */
    @Test
    public void testCrearLibroExitoso() throws Exception {
        String token = obtenirTokenAdmin();

        BookRequest nouLlibre = new BookRequest(
                "Kafka a la platja",
                2,
                "97884984149178",
                "2025-04-09T16:25:59.694Z",
                3,
                true
        );

        Response<Void> response = apiService.createBook(token, nouLlibre).execute();

        if (!response.isSuccessful()) {
            System.out.println("Error al crear llibre:");
            System.out.println("Codi de resposta: " + response.code());
            if (response.errorBody() != null) {
                System.out.println("Cos de l'error: " + response.errorBody().string());
            }
        }

        assertTrue("La creació hauria de ser exitosa", response.isSuccessful());
    }

    /**
     * Comprova que la creació d’un llibre amb camps buits falla amb un error 400.
     */
    @Test
    public void testCrearLibroAmbCampsBuits() throws Exception {
        String token = obtenirTokenAdmin();

        BookRequest llibreInvalid = new BookRequest(
                "", 0, "", "", 0, true
        );

        Response<Void> response = apiService.createBook(token, llibreInvalid).execute();

        assertFalse("La resposta no hauria de ser exitosa amb camps buits", response.isSuccessful());
        assertEquals("El codi de resposta hauria de ser 400", 400, response.code());
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

