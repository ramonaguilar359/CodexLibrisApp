package com.example.codexlibris;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class HistorialReservasActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private Button btnRecomIAnacions;
    private HistorialAdapter adapter;
    private List<LibroReservado> historial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_reservas);

        recyclerView = findViewById(R.id.recyclerHistorial);
        btnRecomIAnacions = findViewById(R.id.btnRecomIAnacions);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);  // -1 és valor per defecte si no existeix

        //userId = 2; // test

        Log.d("Historial", "userId recuperat: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "No s'ha pogut recuperar la sessió de l'usuari", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }





        Map<Integer, List<LibroReservado>> historics = DatosSimulados.obtenerHistoriales();
        historial = historics.getOrDefault(userId, new java.util.ArrayList<>());

        Log.d("Historial", "Llibres carregats: " + historial.size());


        adapter = new HistorialAdapter(historial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnRecomIAnacions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HistorialReservasActivity.this, "Funció de RecomIAnacions encara no implementada", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

