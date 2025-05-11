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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAuthorActivity extends AppCompatActivity {

    private EditText editName, editNationality;
    private TextView textBirthDate;
    private Button btnSelectBirthDate, btnSaveAuthor;

    private String selectedDateISO = null;
    private String token;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_author);

        editName = findViewById(R.id.editName);
        editNationality = findViewById(R.id.editNationality);
        textBirthDate = findViewById(R.id.textBirthDate);
        btnSelectBirthDate = findViewById(R.id.btnSelectBirthDate);
        btnSaveAuthor = findViewById(R.id.btnSaveAuthor);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        btnSelectBirthDate.setOnClickListener(v -> showDatePicker());

        btnSaveAuthor.setOnClickListener(v -> submitAuthor());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    selectedDateISO = date.toString(); // Format: yyyy-MM-dd
                    textBirthDate.setText("Data de naixement: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void submitAuthor() {
        String name = editName.getText().toString().trim();
        String nationality = editNationality.getText().toString().trim();

        if (name.isEmpty() || nationality.isEmpty() || selectedDateISO == null) {
            Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthorRequest request = new AuthorRequest(name, selectedDateISO, nationality);

        //ApiService api = RetrofitClient.getClient().create(ApiService.class);
        ApiService api = RetrofitClient.getClient(context).create(ApiService.class); // ✅

        Call<Void> call = api.createAuthor("Bearer " + token, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateAuthorActivity.this, "Autor creat correctament", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("missatgeExit", "Autor creat correctament");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CreateAuthorActivity.this, "Error en crear autor", Toast.LENGTH_SHORT).show();
                    Log.e("CreateAuthor", "Resposta error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateAuthorActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("CreateAuthor", "Error: " + t.getMessage(), t);
            }
        });
    }
}
