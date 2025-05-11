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

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private List<Genre> genres;
    private int roleId;
    private String token;
    private Context context;

    public GenreAdapter(Context context, List<Genre> genres, int roleId, String token) {
        this.context = context;
        this.genres = genres;
        this.roleId = roleId;
        this.token = token;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.bind(genre);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public void setGenres(List<Genre> newGenres) {
        this.genres = newGenres;
        notifyDataSetChanged();
    }

    class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        Button btnView, btnEdit, btnDelete;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textViewGenreName);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final Genre genre) {
            textName.setText(genre.getName());

            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(context, GenreDetailActivity.class);
                intent.putExtra("GENRE_ID", genre.getId());
                context.startActivity(intent);
            });

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditGenreActivity.class);
                intent.putExtra("genre_id", genre.getId());
                context.startActivity(intent);
            });

            if (roleId == 1) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Confirmació")
                            .setMessage("Vols eliminar aquest gènere?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                int pos = getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    deleteGenre(genre.getId(), pos);
                                }
                            })
                            .setNegativeButton("Cancel·la", null)
                            .show();
                });
            } else {
                btnDelete.setVisibility(View.GONE);
            }
        }

        private void deleteGenre(int genreId, int position) {
            SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);

            //ApiService api = RetrofitClient.getClient().create(ApiService.class);
            ApiService api = RetrofitClient.getClient(context).create(ApiService.class);

            Call<Void> call = api.deleteGenre("Bearer " + token, genreId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        genres.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Gènere eliminat", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error eliminant gènere", Toast.LENGTH_SHORT).show();
                        Log.e("GenreAdapter", "Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error de connexió", Toast.LENGTH_SHORT).show();
                    Log.e("GenreAdapter", "Error de connexió", t);
                }
            });
        }
    }
}
