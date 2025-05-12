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
public class EsdevenimentCrudIntegrationTest {

    private ApiService apiService;
    private String token;

    @Before
    public void setup() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
        token = obtenirTokenAdmin();
    }

    @Test
    public void testCrudCompletEsdeveniment() throws Exception {
        // 1. Crear esdeveniment
        EventRequest nouEvent = new EventRequest("Tertúlia literària", "Debat sobre ciència ficció", "Biblioteca Central", "2025-12-10", "18:00:00", "20:00:00");
        Response<Void> createResponse = apiService.createEvent(token, nouEvent).execute();
        assertTrue("Error en la creació de l'esdeveniment. Codi: " + createResponse.code(), createResponse.isSuccessful());

        // 2. Consultar esdeveniments
        Response<List<Event>> responseList = apiService.getEvents(token).execute();
        assertTrue("Consulta fallida. Codi: " + responseList.code(), responseList.isSuccessful());
        List<Event> esdeveniments = responseList.body();
        assertNotNull("Llista d'esdeveniments nul·la", esdeveniments);
        assertFalse("Llista buida", esdeveniments.isEmpty());

        Event creat = esdeveniments.get(esdeveniments.size() - 1);
        int eventId = creat.getId();
        assertEquals("Títol incorrecte", "Tertúlia literària", creat.getTitle());

        // 3. Actualitzar
        Event actualitzat = new Event(
                eventId,
                "Tertúlia filosòfica",
                "Debat sobre filosofia i literatura",
                "Sala 3",
                "2025-12-11",
                "19:00:00",
                "21:00:00"
        );

        actualitzat.setId(eventId);
        actualitzat.setTitle("Tertúlia filosòfica");
        actualitzat.setDescription("Debat sobre filosofia i literatura");
        actualitzat.setLocation("Sala 3");
        actualitzat.setEvent_date("2025-12-11");
        actualitzat.setStart_time("19:00:00");
        actualitzat.setEnd_time("21:00:00");

        Response<Void> updateResponse = apiService.updateEvent(token, eventId, actualitzat).execute();
        assertTrue("Actualització fallida. Codi: " + updateResponse.code(), updateResponse.isSuccessful());

        // 4. Verificar canvis
        Response<Event> detall = apiService.getEventById(token, eventId).execute();
        assertTrue("Consulta per ID fallida. Codi: " + detall.code(), detall.isSuccessful());
        assertEquals("Títol no actualitzat", "Tertúlia filosòfica", detall.body().getTitle());

        // 5. Eliminar
        Response<Void> deleteResponse = apiService.deleteEvent(token, eventId).execute();
        assertTrue("Eliminació fallida. Codi: " + deleteResponse.code(), deleteResponse.isSuccessful());

        // 6. Verificar que no existeix
        Response<Event> eliminat = apiService.getEventById(token, eventId).execute();
        assertFalse("L'esdeveniment eliminat encara es pot consultar", eliminat.isSuccessful());
        assertEquals("S'esperava 404 per esdeveniment eliminat", 404, eliminat.code());
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