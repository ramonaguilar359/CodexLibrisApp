package com.example.codexlibris;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla que mostra el detall d’un esdeveniment.
 */
public class EventDetailActivity extends AppCompatActivity {

    private TextView textTitle, textDescription, textLocation, textDate, textStartTime, textEndTime;
    private int eventId;
    private String token;

    private Context context;

    /**
     * Inicialitza l’activitat i carrega les dades de l’esdeveniment.
     *
     * Recupera el token i l'ID de l'esdeveniment des de l'intent.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        eventId = getIntent().getIntExtra("event_id", -1);

        if (eventId == -1) {
            Toast.makeText(this, "Error: ID de l’esdeveniment no proporcionat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadEventDetails(eventId);
    }

    /**
     * Assigna les vistes de la interfície gràfica.
     */
    private void initViews() {
        textTitle = findViewById(R.id.textEventTitle);
        textDescription = findViewById(R.id.textEventDescription);
        textLocation = findViewById(R.id.textEventLocation);
        textDate = findViewById(R.id.textEventDate);
        textStartTime = findViewById(R.id.textStartTime);
        textEndTime = findViewById(R.id.textEndTime);
    }

    /**
     * Realitza la crida a l'API per obtenir els detalls de l’esdeveniment.
     *
     * @param id identificador de l’esdeveniment
     */
    private void loadEventDetails(int id) {
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
        Call<Event> call = api.getEventById("Bearer " + token, id);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayEvent(response.body());
                } else {
                    Log.e("EventDetail", "Resposta no vàlida");
                    Toast.makeText(EventDetailActivity.this, "No s’ha pogut carregar l’esdeveniment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("EventDetail", "Error de connexió", t);
                Toast.makeText(EventDetailActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Mostra a la pantalla les dades de l’esdeveniment rebut.
     *
     * @param event objecte Event amb les dades a mostrar
     */
    private void displayEvent(Event event) {
        textTitle.setText(event.getTitle());
        textDescription.setText("Descripció: " + event.getDescription());
        textLocation.setText("Lloc: " + event.getLocation());
        textDate.setText("Data: " + event.getEvent_date());
        textStartTime.setText("Inici: " + event.getStart_time());
        textEndTime.setText("Final: " + event.getEnd_time());
    }
}
