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

public class CreateGenreActivity extends AppCompatActivity {

    private EditText editName, editDescription;
    private Button btnSaveGenre;
    private String token;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_genre);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        editName = findViewById(R.id.editGenreName);
        editDescription = findViewById(R.id.editGenreDescription);
        btnSaveGenre = findViewById(R.id.btnSaveGenre);

        btnSaveGenre.setOnClickListener(v -> submitGenre());
    }

    private void submitGenre() {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        GenreRequest request = new GenreRequest(name, description);

        //ApiService api = RetrofitClient.getClient().create(ApiService.class);
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class); // ✅

        Call<Void> call = api.createGenre("Bearer " + token, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateGenreActivity.this, "Gènere creat correctament", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("missatgeExit", "Gènere creat correctament");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CreateGenreActivity.this, "Error en crear gènere", Toast.LENGTH_SHORT).show();
                    Log.e("CreateGenre", "Resposta error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateGenreActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("CreateGenre", "Error: " + t.getMessage(), t);
            }
        });
    }
}
