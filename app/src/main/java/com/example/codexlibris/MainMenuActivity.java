package com.example.codexlibris;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla principal del menú després d'iniciar sessió.
 * Mostra opcions disponibles com accedir a la gestió de llibres o tancar sessió.
 */
public class MainMenuActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView textWelcome;
    private Button booksButton, logoutButton, btnRecomana, authorsButton, genresButton, btnGestioEsdeveniments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        textWelcome = findViewById(R.id.textWelcome);
        booksButton = findViewById(R.id.booksButton);
        btnRecomana = findViewById(R.id.btnHistorial);
        logoutButton = findViewById(R.id.logoutButton);
        authorsButton = findViewById(R.id.authorsButton);
        genresButton = findViewById(R.id.genresButton);
        btnGestioEsdeveniments = findViewById(R.id.btnGestioEsdeveniments);


        String token = sharedPreferences.getString("jwt_token", null);
        int roleId = sharedPreferences.getInt("role_id", -1);

        if (token != null) { textWelcome.setText("Benvingut!"); }
        if (roleId != 1) { btnRecomana.setVisibility(View.VISIBLE); }

        /**
         * Accés a la pantalla de gestió de llibres.
         */
        booksButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, BooksManagementActivity.class);
            startActivity(intent);
        });

        btnRecomana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HistorialReservasActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Accés a la pantalla de gestió d'autors.
         */
        authorsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, AuthorsManagementActivity.class);
            startActivity(intent);
        });

        /**
         * Accés a la pantalla de gestió de gèneres.
         */
        genresButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, GenresManagementActivity.class);
            startActivity(intent);
        });

        /**
         * Accés a la pantalla de gestió d'esdeveniments.
         */
        btnGestioEsdeveniments.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, EventsManagementActivity.class);
            startActivity(intent);
        });

        /**
         * Elimina les dades de sessió i retorna a la pantalla d'autenticació.
         */
        logoutButton.setOnClickListener(v -> {
            sharedPreferences.edit()
                    .remove("jwt_token")
                    .remove("role_id")
                    .apply();

            Toast.makeText(MainMenuActivity.this, "Token i RoleID esborrats", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

