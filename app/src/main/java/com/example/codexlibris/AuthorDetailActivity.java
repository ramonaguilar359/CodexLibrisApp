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
 * Pantalla de detall d'un autor.
 */
public class AuthorDetailActivity extends AppCompatActivity {

    private TextView textName, textBirthDate, textNationality, textCreatedAt, textUpdatedAt;
    private int authorId;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);

        authorId = getIntent().getIntExtra("AUTHOR_ID", -1);
        if (authorId == -1) {
            Toast.makeText(this, "Error: ID de l'autor no proporcionat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        getAuthorDetails(authorId);
    }

    /**
     * Inicialitza les vistes de la interfície amb els components del layout.
     */
    private void initViews() {
        textName = findViewById(R.id.textName);
        textBirthDate = findViewById(R.id.textBirthDate);
        textNationality = findViewById(R.id.textNationality);
    }

    /**
     * Obté els detalls de l'autor des del servidor mitjançant el seu ID.
     *
     * @param id identificador únic de l'autor
     */
    private void getAuthorDetails(int id) {
        ApiService api = RetrofitClient.getClient(getApplicationContext()).create(ApiService.class);

        Call<Author> call = api.getAuthorById("Bearer " + token, id);

        call.enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                Log.d("AuthorDetail", "Resposta HTTP: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    displayAuthorDetails(response.body());
                } else {
                    Log.e("AuthorDetail", "Resposta no vàlida");
                    Toast.makeText(AuthorDetailActivity.this, "Error en carregar l'autor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {
                Log.e("AuthorDetail", "Error de connexió: " + t.getMessage(), t);
                Toast.makeText(AuthorDetailActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Mostra les dades de l'autor a la interfície.
     *
     * @param author objecte Author amb les dades a visualitzar
     */
    private void displayAuthorDetails(Author author) {
        textName.setText(author.getName());
        textBirthDate.setText("Naixement: " + author.getBirth_date());
        textNationality.setText("Nacionalitat: " + author.getNationality());
    }
}
