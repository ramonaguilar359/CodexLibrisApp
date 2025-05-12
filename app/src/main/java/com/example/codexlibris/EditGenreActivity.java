package com.example.codexlibris;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activitat per editar un gènere existent.
 */
public class EditGenreActivity extends AppCompatActivity {

    private EditText editName, editDescription;
    private Button btnUpdateGenre;
    private int genreId;
    private String token;

    private Context context;

    /**
     * Inicialitza la pantalla i carrega les dades del gènere.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_genre);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        genreId = getIntent().getIntExtra("genre_id", -1);
        if (genreId == -1) {
            Toast.makeText(this, "Error: ID del gènere no proporcionat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editName = findViewById(R.id.editGenreName);
        editDescription = findViewById(R.id.editGenreDescription);
        btnUpdateGenre = findViewById(R.id.btnUpdateGenre);

        carregarDadesDelGenere(genreId);

        btnUpdateGenre.setOnClickListener(v -> actualitzarGenere());
    }

    /**
     * Carrega les dades del gènere des del servidor.
     *
     * @param id ID del gènere a editar
     */
    private void carregarDadesDelGenere(int id) {
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
        api.getGenreById("Bearer " + token, id).enqueue(new Callback<Genre>() {
            @Override
            public void onResponse(Call<Genre> call, Response<Genre> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Genre genre = response.body();
                    editName.setText(genre.getName());
                    editDescription.setText(genre.getDescription());
                } else {
                    Toast.makeText(EditGenreActivity.this, "Error carregant dades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Genre> call, Throwable t) {
                Toast.makeText(EditGenreActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("EditGenre", "Error: ", t);
            }
        });
    }

    /**
     * Valida les dades introduïdes i envia la petició d’actualització del gènere.
     */
    private void actualitzarGenere() {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        GenreRequest request = new GenreRequest(name, description);

        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
        Call<Void> call = api.updateGenre("Bearer " + token, genreId, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditGenreActivity.this, "Gènere actualitzat correctament", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditGenreActivity.this, "Error en actualitzar", Toast.LENGTH_SHORT).show();
                    Log.e("EditGenre", "Resposta error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditGenreActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("EditGenre", "Error: ", t);
            }
        });
    }
}
