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

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editLocation;
    private TextView textEventDate, textStartTime, textEndTime;
    private Button btnSelectDate, btnStartTime, btnEndTime, btnSaveChanges;

    private int eventId;
    private String token;
    private String selectedDateISO, selectedStartTime, selectedEndTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);
        eventId = getIntent().getIntExtra("event_id", -1);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editLocation = findViewById(R.id.editLocation);
        textEventDate = findViewById(R.id.textEventDate);
        textStartTime = findViewById(R.id.textStartTime);
        textEndTime = findViewById(R.id.textEndTime);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnSaveChanges = findViewById(R.id.btnSaveEvent);

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnStartTime.setOnClickListener(v -> showTimePicker(true));
        btnEndTime.setOnClickListener(v -> showTimePicker(false));

        btnSaveChanges.setOnClickListener(v -> submitChanges());

        carregarEvent();
    }

    private void carregarEvent() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getEventById("Bearer " + token, eventId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Event event = response.body();
                    editTitle.setText(event.getTitle());
                    editDescription.setText(event.getDescription());
                    editLocation.setText(event.getLocation());

                    selectedDateISO = event.getEvent_date();
                    selectedStartTime = event.getStart_time();
                    selectedEndTime = event.getEnd_time();

                    textEventDate.setText("Data: " + selectedDateISO);
                    textStartTime.setText("Inici: " + selectedStartTime);
                    textEndTime.setText("Fi: " + selectedEndTime);
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("EditEvent", "Error carregant l'esdeveniment", t);
            }
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDateISO = String.format("%04d-%02d-%02d", year, month + 1, day);
            textEventDate.setText("Data: " + selectedDateISO);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(boolean isStart) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format("%02d:%02d:00", hour, minute);
            if (isStart) {
                selectedStartTime = time;
                textStartTime.setText("Inici: " + time);
            } else {
                selectedEndTime = time;
                textEndTime.setText("Fi: " + time);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void submitChanges() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() ||
                selectedDateISO == null || selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        Event request = new Event(eventId, title, description, location, selectedDateISO, selectedStartTime, selectedEndTime);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.updateEvent("Bearer " + token, eventId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditEventActivity.this, "Esdeveniment actualitzat", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditEventActivity.this, "Error en actualitzar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditEventActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
                Log.e("EditEvent", "Error: ", t);
            }
        });
    }
}
