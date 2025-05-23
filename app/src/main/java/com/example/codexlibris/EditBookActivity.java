package com.example.codexlibris;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activitat per modificar la informació d’un llibre existent.
 * Només disponible per a persones amb rol d’administració.
 */
public class EditBookActivity extends AppCompatActivity {

    // Elements d'interfície
    private EditText editTitle, editISBN;
    private Spinner spinnerAuthor, spinnerGenre;
    private TextView textDate;
    private Button btnSelectDate, btnUpdateBook;
    private Switch switchAvailable;

    // Variables auxiliars
    private int selectedAuthorId = -1;
    private int selectedGenreId = -1;
    private String selectedDateISO = null;
    private int bookId;

    private String token;
    private List<Author> authorList;
    private List<Genre> genreList;

    private Author selectedAuthorObject;
    private Genre selectedGenreObject;

    private Context context;

    /**
     * Inicialitza la pantalla, carrega el llibre a editar i les dades de suport.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);
        bookId = getIntent().getIntExtra("book_id", -1);

        // Vincula les vistes
        editTitle = findViewById(R.id.editTitle);
        editISBN = findViewById(R.id.editISBN);
        spinnerAuthor = findViewById(R.id.spinnerAuthor);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        textDate = findViewById(R.id.textDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnUpdateBook = findViewById(R.id.btnUpdateBook);
        switchAvailable = findViewById(R.id.switchAvailable);

        loadAuthors();
        loadGenres();
        loadBookDetails(bookId);

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnUpdateBook.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String isbn = editISBN.getText().toString().trim();
            boolean available = switchAvailable.isChecked();

            if (title.isEmpty() || isbn.isEmpty() || selectedDateISO == null) {
                Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
                return;
            }

            Book request = new Book();
            request.setId(bookId);
            request.setTitle(title);
            request.setIsbn(isbn);
            request.setPublished_date(selectedDateISO);
            request.setAvailable(available);
            request.setAuthor(selectedAuthorObject);
            request.setGenre(selectedGenreObject);

            //ApiService api = RetrofitClient.getClient().create(ApiService.class);
            ApiService api = RetrofitClient.getClient(context).create(ApiService.class); // ✅

            Call<Void> call = api.updateBook("Bearer " + token, bookId, request);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditBookActivity.this, "Llibre actualitzat!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditBookActivity.this, "Error actualitzant el llibre", Toast.LENGTH_SHORT).show();
                        Log.e("EditBook", "Resposta: " + response.code());
                        try {
                            Log.e("EditBook", "Error cos: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditBookActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                    Log.e("EditBook", "Error: ", t);
                }
            });
        });
    }

    /**
     * Carrega les dades del llibre que es vol editar.
     *
     * @param id ID del llibre
     */
    private void loadBookDetails(int id) {
        //ApiService api = RetrofitClient.getClient().create(ApiService.class);
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
        api.getBookById("Bearer " + token, id).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Book book = response.body();
                    fillFormWithBook(book);

                    selectedAuthorId = book.getAuthor().getId();
                    selectedAuthorObject = book.getAuthor();
                    selectedGenreId = book.getGenre().getId();
                    selectedGenreObject = book.getGenre();
                } else {
                    Log.e("EditBook", "Error carregant llibre");
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Log.e("EditBook", "Error de connexió", t);
            }
        });
    }

    /**
     * Omple els camps de formulari amb les dades del llibre.
     *
     * @param book llibre rebut del servidor
     */
    private void fillFormWithBook(Book book) {
        editTitle.setText(book.getTitle());
        editISBN.setText(book.getIsbn());
        selectedDateISO = book.getPublished_date();
        textDate.setText("Publicat: " + selectedDateISO.split("T")[0]);
        switchAvailable.setChecked(Boolean.TRUE.equals(book.getAvailable()));

        if (authorList != null) {
            for (int i = 0; i < authorList.size(); i++) {
                if (authorList.get(i).getId() == book.getAuthor().getId()) {
                    spinnerAuthor.setSelection(i);
                    selectedAuthorId = book.getAuthor().getId();
                    break;
                }
            }
        }

        if (genreList != null) {
            for (int i = 0; i < genreList.size(); i++) {
                if (genreList.get(i).getId() == book.getGenre().getId()) {
                    spinnerGenre.setSelection(i);
                    selectedGenreId = book.getGenre().getId();
                    break;
                }
            }
        }
    }

    /**
     * Mostra el selector de data per escollir la data de publicació.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    selectedDateISO = date + "T00:00:00";
                    textDate.setText("Publicat: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /**
     * Carrega la llista d'autors i omple el spinner corresponent.
     */
    private void loadAuthors() {
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
        api.getAuthors("Bearer " + token).enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful()) {
                    authorList = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditBookActivity.this,
                            android.R.layout.simple_spinner_item,
                            authorList.stream().map(Author::getName).toArray(String[]::new));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAuthor.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Log.e("EditBook", "Error autors", t);
            }
        });
    }

    /**
     * Carrega la llista de gèneres i omple el spinner corresponent.
     */
    private void loadGenres() {
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
        api.getGenres("Bearer " + token).enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful()) {
                    genreList = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditBookActivity.this,
                            android.R.layout.simple_spinner_item,
                            genreList.stream().map(Genre::getName).toArray(String[]::new));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGenre.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("EditBook", "Error gèneres", t);
            }
        });
    }
}
