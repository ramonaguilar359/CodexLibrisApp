package com.example.codexlibris;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla de detall d'un llibre.
 * Mostra la informació completa d'un llibre seleccionat,
 * incloent autor, gènere, disponibilitat i més.
 */
public class BookDetailActivity extends AppCompatActivity {

    private TextView textTitle, textAuthor, textGenre, textISBN, textPublishedDate, textAvailability;
    private int bookId;
    private SharedPreferences sharedPreferences;
    private int roleId;
    private String token;

    /**
     * Inicialitza la interfície i carrega el detall del llibre.
     * Recupera el token i el roleId des de SharedPreferences.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", null);
        roleId = sharedPreferences.getInt("role_id", -1);

        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId == -1) {
            Toast.makeText(this, "Error: ID del llibre no proporcionat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        getBookDetails(bookId);
    }

    /**
     * Assigna les referències dels TextView de la interfície.
     */
    private void initViews() {
        textTitle = findViewById(R.id.textTitle);
        textAuthor = findViewById(R.id.textAuthor);
        textGenre = findViewById(R.id.textGenre);
        textISBN = findViewById(R.id.textISBN);
        textPublishedDate = findViewById(R.id.textPublishedDate);
        textAvailability = findViewById(R.id.textAvailability);
    }

    /**
     * Fa una crida a l’API per obtenir els detalls del llibre i actualitza la interfície.
     *
     * @param id Identificador del llibre
     */
    private void getBookDetails(int id) {
        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        Log.d("BookDetail", "ID rebut: " + bookId);

        ApiService api = RetrofitClient.getClient(getApplicationContext()).create(ApiService.class);

        Call<Book> call = api.getBookById("Bearer " + token, id);

        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                Log.d("BookDetail", "Resposta HTTP: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Book book = response.body();
                    Log.d("BookDetail", "Títol rebut: " + book.getTitle());
                    displayBookDetails(book);
                } else {
                    Log.e("BookDetail", "Resposta no vàlida");
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Log.e("BookDetail", "Error de connexió: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Mostra les dades del llibre en pantalla.
     *
     * @param book Objecte amb la informació del llibre
     */
    private void displayBookDetails(Book book) {
        textTitle.setText(book.getTitle());
        textAuthor.setText("Autor: " + book.getAuthor().getName());
        textGenre.setText("Gènere: " + book.getGenre().getName());
        textISBN.setText("ISBN: " + book.getIsbn());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        textPublishedDate.setText("Publicat: " + book.getPublished_date());

        textAvailability.setText(book.getAvailable() ? "Disponible" : "Reservat");
        textAvailability.setTextColor(book.getAvailable() ? Color.GREEN : Color.RED);
    }
}
