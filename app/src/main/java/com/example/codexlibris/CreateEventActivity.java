package com.example.codexlibris;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editLocation;
    private TextView textEventDate, textStartTime, textEndTime;
    private Button btnSelectDate, btnStartTime, btnEndTime, btnSaveEvent;

    private String selectedDateISO;
    private String selectedStartTime;
    private String selectedEndTime;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editLocation = findViewById(R.id.editLocation);
        textEventDate = findViewById(R.id.textEventDate);
        textStartTime = findViewById(R.id.textStartTime);
        textEndTime = findViewById(R.id.textEndTime);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnStartTime.setOnClickListener(v -> showTimePicker(true));
        btnEndTime.setOnClickListener(v -> showTimePicker(false));
        btnSaveEvent.setOnClickListener(v -> submitEvent());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    selectedDateISO = date.toString();
                    textEventDate.setText("Data: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isStartTime) {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    LocalTime time = LocalTime.of(hourOfDay, minute);
                    String formatted = time.toString(); // HH:mm:ss
                    if (isStartTime) {
                        selectedStartTime = formatted;
                        textStartTime.setText("Inici: " + time.format(DateTimeFormatter.ofPattern("HH:mm")));
                    } else {
                        selectedEndTime = formatted;
                        textEndTime.setText("Fi: " + time.format(DateTimeFormatter.ofPattern("HH:mm")));
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void submitEvent() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()
                || selectedDateISO == null || selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        EventRequest request = new EventRequest(title, description, location, selectedDateISO, selectedStartTime, selectedEndTime);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = api.createEvent("Bearer " + token, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateEventActivity.this, "Esdeveniment creat correctament", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("missatgeExit", "Esdeveniment creat correctament");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Error en crear esdeveniment", Toast.LENGTH_SHORT).show();
                    Log.e("CreateEvent", "Resposta error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("CreateEvent", "Error: " + t.getMessage(), t);
            }
        });
    }
}
