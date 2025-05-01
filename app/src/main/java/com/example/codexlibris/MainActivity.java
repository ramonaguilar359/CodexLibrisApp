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

/**
 * Pantalla principal d'autenticació.
 * Permet als usuaris iniciar sessió amb nom d'usuari i contrasenya.
 * També inclou placeholders per a funcionalitats futures com l'alta d'usuari i la recuperació de contrasenya.
 */
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

        // patch per no tenir que escriure tota l'estona els valors
        usernameEditText.setText("admin");
        passwordEditText.setText("admin");

        // Inicialització de SharedPreferences per a gestionar la sessió
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        loginButton.setOnClickListener(v -> login());

        altaUsuarioButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Próximamente", Toast.LENGTH_SHORT).show()
        );

        recuperarPasswordButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Próximamente", Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Intenta autenticar l'usuari amb els valors introduïts.
     * Si la resposta és correcta, desa el token i redirigeix al menú principal.
     */
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

                    // fem un log de tot el response per fer un cop d'ull del que retorna el servidor
                    Log.d("Sessio", "Resposta completa: " + response.body());
                    Log.d("Sessio", "User ID (directe): " + response.body().getId());
                    // TODO: demanar a la Jessica que el body response de Login també retorni el userId
                    // de moment fem un patch:
                    String username = response.body().getUsername();
                    int fakeUserId;

                    switch (username) {
                        case "admin":
                            fakeUserId = 1;
                            break;
                        case "geral_rivia":
                            fakeUserId = 2;
                            break;
                        case "laia_miret":
                            fakeUserId = 3;
                            break;
                        case "oriol_sendra":
                            fakeUserId = 4;
                            break;
                        default:
                            fakeUserId = 0;
                            break;
                    }

                    Log.d("Sessio", "Assignant user_id: " + fakeUserId + " per a username: " + username);

                    String token = response.body().getToken();

                    sharedPreferences.edit()
                            .putString("jwt_token", token)
                            .putInt("role_id", response.body().getRoleId())
                            //.putInt("user_id", response.body().getId())
                            .putInt("user_id", fakeUserId) // TODO: Corregir quan el backend estigui llest
                            .apply();

                    Toast.makeText(MainActivity.this, "Login exitoso!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
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

