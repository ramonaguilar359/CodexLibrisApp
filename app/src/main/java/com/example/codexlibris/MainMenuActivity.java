package com.example.codexlibris;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView textWelcome;
    private Button booksButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        // Referencias de la UI
        textWelcome = findViewById(R.id.textWelcome);
        booksButton = findViewById(R.id.booksButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Recuperar token y roleId (se asume que se han guardado en el login)
        String token = sharedPreferences.getString("jwt_token", null);
        int roleId = sharedPreferences.getInt("role_id", -1);

        // Mostrar mensaje de bienvenida
        if (token != null) {
            textWelcome.setText("Benvingut! Token: " + token.substring(0, Math.min(token.length(), 10)) + "...");
        }

        Log.d("BooksManagement", "Cridant a getBooks amb token: Bearer " + token);

        // boto saltar a vista Gestió de Llibres
        booksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, BooksManagementActivity.class);
                startActivity(intent);
            }
        });


        // Si el roleId es 1 (administrador), hacer visible la opción extra
        /* if (roleId == 1) {
            adminOptionButton.setVisibility(View.VISIBLE);
        } else {
            adminOptionButton.setVisibility(View.GONE);
        } */

        // Asignar listeners para las opciones ficticias
        /* option1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Toast.makeText(MainMenuActivity.this, "Opción 1: Pròximament", Toast.LENGTH_SHORT).show(); }
        });

        option2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Toast.makeText(MainMenuActivity.this, "Opción 2: Pròximament", Toast.LENGTH_SHORT).show(); }
        });

        option3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Toast.makeText(MainMenuActivity.this, "Opción 3: Pròximament", Toast.LENGTH_SHORT).show(); }
        });

        option4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Toast.makeText(MainMenuActivity.this, "Opción 4: Pròximament", Toast.LENGTH_SHORT).show(); }
        });

        adminOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Toast.makeText(MainMenuActivity.this, "Opción Administrador: Pròximament", Toast.LENGTH_SHORT).show(); }
        }); */

        // Acción de Logout: Borrar token y roleId, y redirigir al login
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("jwt_token");
                editor.remove("role_id");
                editor.apply();

                // Mostra el missatge informant que s'ha esborrat el token i el roleID
                Toast.makeText(MainMenuActivity.this, "Token i RoleID esborrats", Toast.LENGTH_SHORT).show();

                // Redirigir a la pantalla de login i netejar la pila d'activitats
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
