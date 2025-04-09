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

public class BooksManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BooksAdapter booksAdapter;
    private ExtendedFloatingActionButton fabAddBook;
    private SharedPreferences sharedPreferences;
    private int roleId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_management);

        // Recuperem les dades de sessió (token i roleId)
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);
        roleId = sharedPreferences.getInt("role_id", -1);

        // Trobar les vistes
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        fabAddBook = findViewById(R.id.fabAddBook);

        // Configurar el RecyclerView amb un LinearLayoutManager
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        // Assignem un adaptador buit inicialment
        booksAdapter = new BooksAdapter(new ArrayList<>(), roleId, token);
        recyclerViewBooks.setAdapter(booksAdapter);

        // Mostrar el botó flotant només per a administradors (roleId == 1)
        if (roleId == 1) {
            fabAddBook.setVisibility(android.view.View.VISIBLE);
        } else {
            fabAddBook.setVisibility(android.view.View.GONE);
        }

        // Botó flotant per crear un nou llibre (obre NewBookActivity)
        fabAddBook.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewBookActivity.class);
            startActivityForResult(intent, 1);

        });

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

    @Override
    protected void onResume() {
        super.onResume();
        carregarLlibres();
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
                // I opcionalment tornar a carregar els llibres:
                carregarLlibres();
            }
        }
    }


    private void carregarLlibres() {
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);

        if (token == null) {
            Log.e("BooksManagement", "Token nul. No es pot carregar llibres.");
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Log.d("BooksManagement", "Cridant a getBooks amb token: Bearer " + token);
        Call<List<Book>> call = apiService.getBooks("Bearer " + token);

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.d("BooksManagement", "Resposta HTTP: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> llibres = response.body();

                    Collections.reverse(llibres); // invertim la llista per veure primer el més recent

                    Log.d("BooksManagement", "Llibres rebuts: " + llibres.size());
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

    // perquè no funcionen els botons de l'emulador?
    /*
    @Override
    public void onBackPressed() {
        Log.d("BookDetail", "S'ha premut el botó enrere");
        super.onBackPressed();
    }
    */
}
