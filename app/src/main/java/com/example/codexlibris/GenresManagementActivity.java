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
 * Pantalla principal per gestionar els gèneres disponibles.
 */
public class GenresManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGenres;
    private GenreAdapter genreAdapter;
    private ExtendedFloatingActionButton fabAddGenre;
    private SharedPreferences sharedPreferences;
    private int roleId;
    private String token;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres_management);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);
        roleId = sharedPreferences.getInt("role_id", -1);

        recyclerViewGenres = findViewById(R.id.recyclerViewGenres);
        fabAddGenre = findViewById(R.id.fabAddGenre);

        recyclerViewGenres.setLayoutManager(new LinearLayoutManager(this));

        genreAdapter = new GenreAdapter(this, new ArrayList<>(), roleId, token);
        recyclerViewGenres.setAdapter(genreAdapter);

        fabAddGenre.setVisibility(roleId == 1 ? View.VISIBLE : View.GONE);
        fabAddGenre.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateGenreActivity.class);
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

        carregarGenres();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarGenres();
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
                carregarGenres();
            }
        }
    }

    /**
     * Carrega la llista de gèneres des del servidor i l'assigna a l'adaptador.
     *
     * Comprova si hi ha token disponible i realitza una crida a l'API per obtenir
     * els gèneres. Mostra missatges d'error si la càrrega falla.
     */
    private void carregarGenres() {
        token = sharedPreferences.getString("jwt_token", null);
        if (token == null) {
            Log.e("GenresManagement", "Token nul. No es pot carregar gèneres.");
            return;
        }

        //ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<List<Genre>> call = apiService.getGenres("Bearer " + token);

        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> generes = response.body();
                    Collections.reverse(generes);
                    genreAdapter.setGenres(generes);
                } else {
                    Toast.makeText(GenresManagementActivity.this, "Error en carregar els gèneres", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Toast.makeText(GenresManagementActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
