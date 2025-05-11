package com.example.codexlibris;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activitat per editar un autor existent.
 */
public class EditAuthorActivity extends AppCompatActivity {

    private EditText editName, editNationality;
    private TextView textBirthDate;
    private Button btnSelectDate, btnUpdateAuthor;

    private int authorId;
    private String token;
    private String selectedDateISO = null;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_author);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);
        authorId = getIntent().getIntExtra("author_id", -1);

        editName = findViewById(R.id.editAuthorName);
        editNationality = findViewById(R.id.editAuthorNationality);
        textBirthDate = findViewById(R.id.textAuthorBirthDate);
        btnSelectDate = findViewById(R.id.btnSelectBirthDate);
        btnUpdateAuthor = findViewById(R.id.btnUpdateAuthor);

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnUpdateAuthor.setOnClickListener(v -> updateAuthor());

        loadAuthorDetails(authorId);
    }

    private void loadAuthorDetails(int id) {
        // ApiService api = RetrofitClient.getClient().create(ApiService.class);
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class);

        api.getAuthorById("Bearer " + token, id).enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Author author = response.body();
                    fillFormWithAuthor(author);
                } else {
                    Toast.makeText(EditAuthorActivity.this, "Error en carregar l'autor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {
                Log.e("EditAuthor", "Error de connexió", t);
                Toast.makeText(EditAuthorActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillFormWithAuthor(Author author) {
        editName.setText(author.getName());
        editNationality.setText(author.getNationality());

        selectedDateISO = author.getBirth_date();
        if (selectedDateISO != null) {
            textBirthDate.setText("Naixement: " + selectedDateISO);
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    selectedDateISO = date.toString(); // format: YYYY-MM-DD
                    textBirthDate.setText("Naixement: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateAuthor() {
        String name = editName.getText().toString().trim();
        String nationality = editNationality.getText().toString().trim();

        if (name.isEmpty() || nationality.isEmpty() || selectedDateISO == null) {
            Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        Author updated = new Author();
        updated.setId(authorId);
        updated.setName(name);
        updated.setNationality(nationality);
        updated.setBirth_date(selectedDateISO);

        //ApiService api = RetrofitClient.getClient().create(ApiService.class);
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class); // ✅

        api.updateAuthor("Bearer " + token, authorId, updated).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditAuthorActivity.this, "Autor actualitzat correctament", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditAuthorActivity.this, "Error actualitzant l'autor", Toast.LENGTH_SHORT).show();
                    Log.e("EditAuthor", "Resposta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditAuthorActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("EditAuthor", "Error: ", t);
            }
        });
    }
}
