package com.example.codexlibris;

import android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;



public class HistorialReservasActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private Button btnRecomIAnacions;
    private HistorialAdapter adapter;
    private List<LibroReservado> historial;

    private static final String OPENAI_API_KEY = "...";
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_MODEL = "gpt-3.5-turbo";


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
                btnRecomIAnacions.setEnabled(false);
                btnRecomIAnacions.setText("Carregant...");

                StringBuilder prompt = new StringBuilder();
                prompt.append("Aquests són alguns llibres que ha llegit un usuari:\n");

                for (LibroReservado llibre : historial) {
                    prompt.append("- ").append(llibre.getTitle()).append(" de ").append(llibre.getAuthor()).append("\n");
                }

                prompt.append("\nRecomana 3 llibres nous d’autors diferents però amb temàtiques similars. Només la llista.");

                // Crida a la IA en segon pla
                ferRecomanacioIA(prompt.toString());

            }
        });
    }

    private void ferRecomanacioIA(String prompt) {
        OkHttpClient client = new OkHttpClient();

        // ✅ Construim el JSON correctament
        JSONObject message = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONArray messages = new JSONArray();

        try {
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);

            payload.put("model", OPENAI_MODEL);
            payload.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("ChatGPT", "JSON enviat:\n" + payload.toString());

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, payload.toString());

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Log.d("ChatGPT", "Cridant l'API de ChatGPT...");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ChatGPT", "Error de connexió amb l'API", e);
                runOnUiThread(() -> {
                    btnRecomIAnacions.setEnabled(true);
                    btnRecomIAnacions.setText("Fes-me recomanacions amb IA");
                    Toast.makeText(HistorialReservasActivity.this, "Error de connexió amb ChatGPT", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("ChatGPT", "Codi HTTP resposta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String resposta = response.body().string();
                    Log.d("ChatGPT", "Resposta JSON:\n" + resposta);

                    String recomanacio = parseRespostaChatGPT(resposta);
                    runOnUiThread(() -> mostrarRecomanacio(recomanacio));
                } else {
                    Log.e("ChatGPT", "Error de resposta: " + response.code());
                    if (response.body() != null) {
                        Log.e("ChatGPT", "Cos de la resposta:\n" + response.body().string());
                    }
                    runOnUiThread(() -> {
                        btnRecomIAnacions.setEnabled(true);
                        btnRecomIAnacions.setText("RecomIAnacions");
                        Toast.makeText(HistorialReservasActivity.this, "Error en la resposta de ChatGPT", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }



    private String parseRespostaChatGPT(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray choices = obj.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            return message.getString("content").trim();
        } catch (JSONException e) {
            e.printStackTrace();
            return "No s'ha pogut interpretar la resposta.";
        }
    }

    private void mostrarRecomanacio(String text) {
        btnRecomIAnacions.setEnabled(true);
        btnRecomIAnacions.setText("RecomIAnacions");

        new AlertDialog.Builder(this)
                .setTitle("Recomanacions amb IA")
                .setMessage(text)
                .setPositiveButton("Tancar", null)
                .show();
    }



}

