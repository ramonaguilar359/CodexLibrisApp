package com.example.codexlibris;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Response;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LoginIntegrationTest {

    private ApiService apiService;

    @Before
    public void setup() {
        // Usamos el RetrofitClient ya configurado para conectarnos al backend
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @Test
    public void testLoginExitoso() throws Exception {
        // Login correcto: usuario y contraseña válidos
        LoginRequest request = new LoginRequest("admin", "admin");
        Response<LoginResponse> response = apiService.login(request).execute();

        // Comprobamos que la respuesta sea exitosa y que se reciba el token
        assertTrue("La respuesta debería ser exitosa", response.isSuccessful());
        LoginResponse loginResponse = response.body();
        assertNotNull("El cuerpo de la respuesta no debería ser nulo", loginResponse);
        assertNotNull("El token no debería ser nulo", loginResponse.getToken());
        assertFalse("El token no debería estar vacío", loginResponse.getToken().isEmpty());
    }

    @Test
    public void testLoginFallido() throws Exception {
        // Login fallido: contraseña incorrecta
        LoginRequest request = new LoginRequest("admin", "incorrectPassword");
        Response<LoginResponse> response = apiService.login(request).execute();

        // Se espera que la respuesta no sea exitosa y devuelva 401 (Unauthorized)
        assertFalse("La respuesta no debería ser exitosa", response.isSuccessful());
        assertEquals("El código de respuesta debería ser 401", 401, response.code());
    }

    @Test
    public void testLoginConCamposVacios() throws Exception {
        // Login con campos vacíos
        LoginRequest request = new LoginRequest("", "");
        Response<LoginResponse> response = apiService.login(request).execute();

        // Se espera que la respuesta no sea exitosa
        assertFalse("La respuesta no debería ser exitosa con campos vacíos", response.isSuccessful());
        // Comprobamos que el código de respuesta no sea 200 (éxito)
        assertNotEquals("El código de respuesta no debería ser 200", 200, response.code());
    }
}
