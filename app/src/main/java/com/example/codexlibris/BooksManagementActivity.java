package com.example.codexlibris;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BooksAdapter booksAdapter;
    private FloatingActionButton fabAddBook;
    private SharedPreferences sharedPreferences;
    private int roleId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_management);

        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        fabAddBook = findViewById(R.id.fabAddBook);

        // Recuperem les dades de sessió (token i roleId)
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);
        roleId = sharedPreferences.getInt("role_id", -1);

        // Mostrem el botó flotant només per a administradors (roleId == 1)
        if (roleId == 1) {
            fabAddBook.setVisibility(android.view.View.VISIBLE);
        } else {
            fabAddBook.setVisibility(android.view.View.GONE);
        }

        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));

        // Botó flotant per crear un nou llibre (obrir Activity per crear llibre nou)
        fabAddBook.setOnClickListener(view -> {
            Intent intent = new Intent(BooksManagementActivity.this, NewBookActivity.class);
            startActivity(intent);
        });

        carregarLlibres();
    }

    private void carregarLlibres() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        // Crida a l'endpoint GET /books
        Call<List<Book>> call = apiService.getBooks();

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> llibres = response.body();
                    booksAdapter = new BooksAdapter(llibres, roleId, token);
                    recyclerViewBooks.setAdapter(booksAdapter);
                } else {
                    Toast.makeText(BooksManagementActivity.this, "Error en carregar els llibres", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(BooksManagementActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

