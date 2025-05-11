package com.example.codexlibris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.*;
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
 * Adaptador per mostrar una llista d'autors amb accions per a administradors.
 */
public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {

    private Context context;
    private final List<Author> authors;
    private final int roleId;
    private final String token;

    public AuthorAdapter(Context context, List<Author> authors, int roleId, String token) {
        this.context = context;
        this.authors = authors;
        this.roleId = roleId;
        this.token = token;
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_author, parent, false);
        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
        holder.bind(authors.get(position));
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    public void setAuthors(List<Author> newAuthors) {
        authors.clear();
        authors.addAll(newAuthors);
        notifyDataSetChanged();
    }

    class AuthorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        Button btnView, btnEdit, btnDelete;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewAuthorName);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final Author author) {
            nameTextView.setText(author.getName());

            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AuthorDetailActivity.class);
                intent.putExtra("AUTHOR_ID", author.getId());
                context.startActivity(intent);
            });

            if (roleId == 1) {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);

                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(context, EditAuthorActivity.class);
                    intent.putExtra("author_id", author.getId());
                    context.startActivity(intent);
                });

                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Confirmació")
                            .setMessage("Vols eliminar aquest autor?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                int pos = getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    deleteAuthor(author.getId(), pos);
                                }
                            })
                            .setNegativeButton("Cancel·la", null)
                            .show();
                });

            } else {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }

        private void deleteAuthor(int authorId, int position) {
            //ApiService api = RetrofitClient.getClient().create(ApiService.class);
            ApiService api = RetrofitClient.getClient(context).create(ApiService.class);

            Call<Void> call = api.deleteAuthor("Bearer " + token, authorId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Autor esborrat", Toast.LENGTH_SHORT).show();
                        authors.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Toast.makeText(context, "Error esborrant l'autor", Toast.LENGTH_SHORT).show();
                        Log.e("AuthorAdapter", "Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error de connexió", Toast.LENGTH_SHORT).show();
                    Log.e("AuthorAdapter", "Error esborrant", t);
                }
            });
        }
    }
}
