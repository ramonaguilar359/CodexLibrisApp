package com.example.codexlibris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adaptador per a mostrar una llista d'esdeveniments amb opcions de visualització, edició i eliminació.
 *
 * @param context el context des d'on s'utilitza l'adaptador
 * @param events la llista d'esdeveniments a mostrar
 * @param roleId l'ID del rol de l'usuari (1 = administrador)
 * @param token el token JWT per a l'autenticació amb l'API
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;
    private int roleId;
    private String token;
    private Context context;

    public EventAdapter(Context context, List<Event> events, int roleId, String token) {
        this.context = context;
        this.events = events;
        this.roleId = roleId;
        this.token = token;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Actualitza la llista d'esdeveniments i refresca la vista.
     *
     * @param events nova llista d'esdeveniments
     */
    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder que representa cada fila de la llista d'esdeveniments.
     */
    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDateTime, textLocation;
        Button btnView, btnEdit, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textEventTitle);
            textDateTime = itemView.findViewById(R.id.textEventDateTime);
            textLocation = itemView.findViewById(R.id.textEventLocation);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        /**
         * Assigna les dades d'un esdeveniment a la vista.
         *
         * @param event esdeveniment a mostrar
         */
        public void bind(final Event event) {
            textTitle.setText(event.getTitle());
            textDateTime.setText(event.getEvent_date() + " · " + event.getStart_time() + " - " + event.getEnd_time());
            textLocation.setText(event.getLocation());

            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("event_id", event.getId());
                Log.d("EventAdapter", "ID enviat a EventDetailActivity: " + event.getId());
                context.startActivity(intent);
            });

            if (roleId == 1) {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);

                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(context, EditEventActivity.class);
                    intent.putExtra("event_id", event.getId());
                    context.startActivity(intent);
                });

                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Confirmació")
                            .setMessage("Vols eliminar aquest esdeveniment?")
                            .setPositiveButton("Sí", (dialog, which) -> deleteEvent(event.getId(), getAdapterPosition()))
                            .setNegativeButton("Cancel·la", null)
                            .show();
                });
            } else {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }

        /**
         * Elimina un esdeveniment de la llista i del servidor.
         *
         * @param id identificador de l'esdeveniment
         * @param position posició de l'ítem en la llista
         */
        private void deleteEvent(int id, int position) {
            SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);

            ApiService api = RetrofitClient.getClient(context).create(ApiService.class);
            Call<Void> call = api.deleteEvent("Bearer " + token, id);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Esdeveniment eliminat", Toast.LENGTH_SHORT).show();
                        events.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Toast.makeText(context, "Error eliminant", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error de connexió", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
