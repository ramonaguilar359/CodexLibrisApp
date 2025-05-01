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
 * Pantalla principal per gestionar els autors disponibles.
 */
public class AuthorsManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAuthors;
    private AuthorAdapter authorsAdapter;
    private ExtendedFloatingActionButton fabAddAuthor;
    private SharedPreferences sharedPreferences;
    private int roleId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors_management);

        // Recuperar dades de sessió
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);
        roleId = sharedPreferences.getInt("role_id", -1);

        recyclerViewAuthors = findViewById(R.id.recyclerViewAuthors);
        fabAddAuthor = findViewById(R.id.fabAddAuthor);

        recyclerViewAuthors.setLayoutManager(new LinearLayoutManager(this));
        authorsAdapter = new AuthorAdapter(this, new ArrayList<>(), roleId, token);
        recyclerViewAuthors.setAdapter(authorsAdapter);

        // Mostrar o amagar botó de crear autor segons el rol
        fabAddAuthor.setVisibility(roleId == 1 ? View.VISIBLE : View.GONE);

        fabAddAuthor.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateAuthorActivity.class);
            startActivityForResult(intent, 1);
        });

        // Missatge d'èxit des d'una altra activitat
        String missatgeExit = getIntent().getStringExtra("missatgeExit");
        if (missatgeExit != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Èxit")
                    .setMessage(missatgeExit)
                    .setPositiveButton("D'acord", null)
                    .show();
        }

        carregarAutors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarAutors();
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
                carregarAutors();
            }
        }
    }

    /**
     * Carrega la llista d’autors des del servidor.
     */
    private void carregarAutors() {
        token = sharedPreferences.getString("jwt_token", null);
        if (token == null) {
            Log.e("AuthorsManagement", "Token nul. No es pot carregar autors.");
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Author>> call = apiService.getAuthors("Bearer " + token);

        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Author> autors = response.body();
                    Collections.reverse(autors); // Mostrar els més recents primer
                    authorsAdapter.setAuthors(autors);
                } else {
                    Toast.makeText(AuthorsManagementActivity.this, "Error en carregar els autors", Toast.LENGTH_SHORT).show();
                    Log.e("AuthorsManagement", "Error en la resposta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(AuthorsManagementActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("AuthorsManagement", "Error de connexió: " + t.getMessage(), t);
            }
        });
    }
}
