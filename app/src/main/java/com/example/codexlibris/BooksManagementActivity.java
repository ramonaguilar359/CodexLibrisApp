package com.example.codexlibris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
 * Pantalla principal per gestionar els llibres disponibles.
 * Mostra un llistat amb opcions segons el rol de l'usuari: crear, editar, esborrar o reservar.
 */
public class BooksManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BooksAdapter booksAdapter;
    private ExtendedFloatingActionButton fabAddBook;
    private SharedPreferences sharedPreferences;
    private int roleId;
    private String token;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_management);

        // Recuperem les dades de sessió
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);
        roleId = sharedPreferences.getInt("role_id", -1);

        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        fabAddBook = findViewById(R.id.fabAddBook);

        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        booksAdapter = new BooksAdapter(new ArrayList<>(), roleId, token);
        recyclerViewBooks.setAdapter(booksAdapter);

        // Només els administradors poden afegir llibres
        fabAddBook.setVisibility(roleId == 1 ? android.view.View.VISIBLE : android.view.View.GONE);

        fabAddBook.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewBookActivity.class);
            startActivityForResult(intent, 1);
        });

        // Mostrar missatge d'èxit si es retorna des d'una altra activitat
        String missatgeExit = getIntent().getStringExtra("missatgeExit");
        if (missatgeExit != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Èxit")
                    .setMessage(missatgeExit)
                    .setPositiveButton("D'acord", null)
                    .show();
        }

        carregarLlibres();
    }

    /**
     * Quan es torna a aquesta activitat, es recarrega la llista de llibres.
     */
    @Override
    protected void onResume() {
        super.onResume();
        carregarLlibres();
    }

    /**
     * Gestiona el retorn des de l'activitat de creació d'un nou llibre.
     * Si s'ha creat amb èxit, es mostra un missatge i es refresca la llista.
     */
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
                carregarLlibres();
            }
        }
    }

    /**
     * Carrega la llista de llibres des del servidor.
     * Mostra un error si hi ha problemes de connexió o autenticació.
     */
    private void carregarLlibres() {
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);

        if (token == null) {
            Log.e("BooksManagement", "Token nul. No es pot carregar llibres.");
            return;
        }

        //ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);


        Log.d("BooksManagement", "Cridant a getBooks amb token: Bearer " + token);
        Call<List<Book>> call = apiService.getBooks("Bearer " + token);

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.d("BooksManagement", "Resposta HTTP: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> llibres = response.body();
                    Collections.reverse(llibres); // Mostrar primer els més recents
                    booksAdapter = new BooksAdapter(llibres, roleId, token);
                    recyclerViewBooks.setAdapter(booksAdapter);
                } else {
                    Log.e("BooksManagement", "Error en la resposta: " + response.message());
                    Toast.makeText(BooksManagementActivity.this, "Error en carregar els llibres", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("BooksManagement", "Error de connexió: " + t.getMessage(), t);
                Toast.makeText(BooksManagementActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    // Descomentar si cal controlar el botó enrere de forma específica
    @Override
    public void onBackPressed() {
        Log.d("BookDetail", "S'ha premut el botó enrere");
        super.onBackPressed();
    }
    */
}
