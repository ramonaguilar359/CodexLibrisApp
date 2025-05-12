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
 * Pantalla que mostra el detall d’un gènere.
 * Recupera la informació des del servidor a partir de l'ID del gènere passat per intent.
 */
public class GenreDetailActivity extends AppCompatActivity {

    private TextView textName, textDescription, textCreatedAt, textUpdatedAt;
    private int genreId;
    private String token;

    private Context context;

    /**
     * Inicialitza la vista i inicia la càrrega del gènere.
     * @param savedInstanceState estat anterior de l'activitat, si existeix
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_detail);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        genreId = getIntent().getIntExtra("GENRE_ID", -1);
        if (genreId == -1) {
            Toast.makeText(this, "ID del gènere no proporcionat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        carregarDetallGenere(genreId);
    }

    /**
     * Inicialitza les referències als elements de la interfície.
     */
    private void initViews() {
        textName = findViewById(R.id.textName);
        textDescription = findViewById(R.id.textDescription);
    }

    /**
     * Fa una crida a l'API per obtenir el detall del gènere amb el seu ID.
     * @param id identificador del gènere
     */
    private void carregarDetallGenere(int id) {
        //ApiService api = RetrofitClient.getClient().create(ApiService.class);
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
        Call<Genre> call = api.getGenreById("Bearer " + token, id);

        call.enqueue(new Callback<Genre>() {
            @Override
            public void onResponse(Call<Genre> call, Response<Genre> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarDetall(response.body());
                } else {
                    Toast.makeText(GenreDetailActivity.this, "Error obtenint el gènere", Toast.LENGTH_SHORT).show();
                    Log.e("GenreDetail", "Resposta error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Genre> call, Throwable t) {
                Toast.makeText(GenreDetailActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("GenreDetail", "Error connexió", t);
            }
        });
    }

    /**
     * Mostra la informació del gènere a la pantalla.
     * @param genre objecte {@link Genre} amb les dades a mostrar
     */
    private void mostrarDetall(Genre genre) {
        textName.setText(genre.getName());
        textDescription.setText(genre.getDescription());
    }
}
