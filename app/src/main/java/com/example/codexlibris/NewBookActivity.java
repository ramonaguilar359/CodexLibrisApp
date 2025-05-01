package com.example.codexlibris;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewBookActivity extends AppCompatActivity {

    private EditText editTitle, editISBN;
    private Spinner spinnerAuthor, spinnerGenre;
    private TextView textDate;
    private Button btnSelectDate, btnSaveBook;
    private Switch switchAvailable;

    private int selectedAuthorId = -1;
    private int selectedGenreId = -1;
    private String selectedDateISO = null;

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);

        // Inicialitzar vistes
        editTitle = findViewById(R.id.editTitle);
        editISBN = findViewById(R.id.editISBN);
        spinnerAuthor = findViewById(R.id.spinnerAuthor);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        textDate = findViewById(R.id.textDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSaveBook = findViewById(R.id.btnSaveBook);
        switchAvailable = findViewById(R.id.switchAvailable);

        // Obtenir token
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        // hem de carregar els autors i generes per fer-los servir al formulari
        loadAuthors();
        loadGenres();

        // Selecció de data
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Enviar llibre
        btnSaveBook.setOnClickListener(v -> submitBook());
    }

    private void loadAuthors() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAuthors("Bearer " + token).enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                Log.d("NewBook", "Resposta AUTORS: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Author> authors = response.body();
                    Log.d("NewBook", "Autors rebuts: " + authors.size());
                    for (Author a : authors) {
                        Log.d("NewBook", "Autor: " + a.getId() + " - " + a.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(NewBookActivity.this,
                            android.R.layout.simple_spinner_item,
                            authors.stream().map(Author::getName).toArray(String[]::new));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAuthor.setAdapter(adapter);

                    spinnerAuthor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedAuthorId = authors.get(position).getId();
                            Log.d("NewBook", "Autor seleccionat: " + selectedAuthorId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    Log.e("NewBook", "Error resposta autors: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Log.e("NewBook", "Error connexió autors: " + t.getMessage(), t);
            }
        });
    }

    private void loadGenres() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getGenres("Bearer " + token).enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                Log.d("NewBook", "Resposta GÈNERES: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = response.body();
                    Log.d("NewBook", "Gèneres rebuts: " + genres.size());
                    for (Genre g : genres) {
                        Log.d("NewBook", "Gènere: " + g.getId() + " - " + g.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(NewBookActivity.this,
                            android.R.layout.simple_spinner_item,
                            genres.stream().map(Genre::getName).toArray(String[]::new));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGenre.setAdapter(adapter);

                    spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedGenreId = genres.get(position).getId();
                            Log.d("NewBook", "Gènere seleccionat: " + selectedGenreId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    Log.e("NewBook", "Error resposta gèneres: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("NewBook", "Error connexió gèneres: " + t.getMessage(), t);
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Format ISO amb hora 00:00:00
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    selectedDateISO = date.atStartOfDay().toString(); // ISO 8601
                    textDate.setText("Data de publicació: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void submitBook() {
        String title = editTitle.getText().toString().trim();
        String isbn = editISBN.getText().toString().trim();
        boolean available = switchAvailable.isChecked();

        // De moment, simulem autorId i genreId perquè encara no hem carregat-los
        int authorId = selectedAuthorId != -1 ? selectedAuthorId : 1;
        int genreId = selectedGenreId != -1 ? selectedGenreId : 1;

        if (title.isEmpty() || isbn.isEmpty() || selectedDateISO == null) {
            Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        BookRequest request = new BookRequest(title, authorId, isbn, selectedDateISO, genreId, available);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.createBook("Bearer " + token, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewBookActivity.this, "Llibre creat correctament", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("missatgeExit", "Llibre creat correctament");
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    Toast.makeText(NewBookActivity.this, "Error en crear llibre", Toast.LENGTH_SHORT).show();
                    Log.e("NewBook", "Resposta error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(NewBookActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("NewBook", "Error: " + t.getMessage(), t);
            }
        });
    }
}
