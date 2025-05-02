package com.example.codexlibris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla principal per gestionar els esdeveniments disponibles.
 */
public class EventsManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private ExtendedFloatingActionButton fabAddEvent;
    private SharedPreferences sharedPreferences;
    private int roleId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_management);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);
        roleId = sharedPreferences.getInt("role_id", -1);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        fabAddEvent = findViewById(R.id.fabAddEvent);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(this, new ArrayList<>(), roleId, token);
        recyclerViewEvents.setAdapter(eventAdapter);

        fabAddEvent.setVisibility(roleId == 1 ? View.VISIBLE : View.GONE);
        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivityForResult(intent, 1);
        });

        String missatgeExit = getIntent().getStringExtra("missatgeExit");
        if (missatgeExit != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Èxit")
                    .setMessage(missatgeExit)
                    .setPositiveButton("D'acord", null)
                    .show();
        }

        carregarEsdeveniments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEsdeveniments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String missatge = data.getStringExtra("missatgeExit");
            if (missatge != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Èxit")
                        .setMessage(missatge)
                        .setPositiveButton("D'acord", null)
                        .show();
                carregarEsdeveniments();
            }
        }
    }

    private void carregarEsdeveniments() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Event>> call = api.getEvents("Bearer " + token);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> esdeveniments = response.body();
                    Collections.reverse(esdeveniments);
                    eventAdapter.setEvents(esdeveniments);
                } else {
                    Toast.makeText(EventsManagementActivity.this, "Error en carregar esdeveniments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(EventsManagementActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("EventsManagement", "Error: ", t);
            }
        });
    }
}
