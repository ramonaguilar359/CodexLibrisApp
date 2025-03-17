package com.example.codexlibris;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton, altaUsuarioButton, recuperarPasswordButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        altaUsuarioButton = findViewById(R.id.altaUsuarioButton);
        recuperarPasswordButton = findViewById(R.id.recuperarPasswordButton);

        // Inicializar SharedPreferences para guardar el token
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Botones "Alta usuario" y "Recuperar contraseña"
        altaUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Próximamente", Toast.LENGTH_SHORT).show();
            }
        });

        recuperarPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Próximamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        Log.d(TAG, "Intentando login con usuario: " + username);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(username, password);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Código de respuesta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    Log.d(TAG, "Respuesta de la API: " + response.body().toString());

                    String token = response.body().getToken();
                    Log.d(TAG, "Token recibido: " + token);

                    // Guardar el token en SharedPreferences
                    sharedPreferences.edit()
                            .putString("jwt_token", token)
                            .putInt("role_id", response.body().getRoleId())
                            .apply();

                    Toast.makeText(MainActivity.this, "Login exitoso!", Toast.LENGTH_SHORT).show();

                    // Saltar a la pantalla del menú principal
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e(TAG, "Error en login: Código HTTP " + response.code());
                    try {
                        Log.e(TAG, "Cuerpo de respuesta: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error al leer el cuerpo de respuesta", e);
                    }
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Error en la conexión", t);
                Toast.makeText(MainActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
