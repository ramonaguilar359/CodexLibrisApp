package com.example.codexlibris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private List<Book> llibres;
    private int roleId;
    private String token;
    private Context context;

    public BooksAdapter(List<Book> llibres, int roleId, String token) {
        this.llibres = llibres;
        this.roleId = roleId;
        this.token = token;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book llibre = llibres.get(position);
        holder.bind(llibre);
    }

    @Override
    public int getItemCount() {
        return llibres.size();
    }

    public void updateBooks(List<Book> newBooks) {
        this.llibres.clear();
        this.llibres.addAll(newBooks);
        notifyDataSetChanged();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        Button btnViewDetail, btnEdit, btnDelete, btnReserve;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }

        public void bind(final Book llibre) {
            textTitle.setText(llibre.getTitle());

            TextView textAvailability = itemView.findViewById(R.id.textAvailability);
            if (llibre.getAvailable()) {
                textAvailability.setText("Disponible");
                textAvailability.setTextColor(Color.GREEN);
            } else {
                textAvailability.setText("Reservat");
                textAvailability.setTextColor(Color.RED);
            }

            // Tots poden veure el detall del llibre
            btnViewDetail.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), BookDetailActivity.class);
                intent.putExtra("BOOK_ID", llibre.getId());
                itemView.getContext().startActivity(intent);
            });

            if (roleId == 1) {
                // Administrador: es mostra editar i esborrar
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnReserve.setVisibility(View.GONE);

                btnEdit.setOnClickListener(v ->
                        Toast.makeText(context, "Editar llibre: " + llibre.getTitle(), Toast.LENGTH_SHORT).show()
                );
                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Confirmació")
                            .setMessage("Estàs segur que vols eliminar aquest llibre?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                int pos = getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    deleteBook(llibre.getId(), pos);
                                }
                            })

                            .setNegativeButton("Cancel·la", null)
                            .show();
                });

            } else {
                // Usuari: es mostra reservar
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                btnReserve.setVisibility(View.VISIBLE);

                btnReserve.setOnClickListener(v ->
                        Toast.makeText(context, "Reservar llibre: " + llibre.getTitle(), Toast.LENGTH_SHORT).show()
                );
            }

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditBookActivity.class);
                intent.putExtra("book_id", llibre.getId());
                context.startActivity(intent);
            });

        }

        private void deleteBook(int bookId, int position) {
            SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            Call<Void> call = api.deleteBook("Bearer " + token, bookId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        DialogUtils.showMessage(context, "Llibre esborrat");

                        llibres.remove(position);
                        notifyItemRemoved(position);

                    } else {
                        Toast.makeText(context, "Error esborrant", Toast.LENGTH_SHORT).show();
                        Log.e("BookAdapter", "Resposta error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error de connexió", Toast.LENGTH_SHORT).show();
                    Log.e("BookAdapter", "Error esborrant llibre", t);
                }
            });
        }

        private void reserveBook(Book book) {
            // Missatge temporal mentre el backend no està llest
            new AlertDialog.Builder(context)
                    .setTitle("Funcionalitat no disponible")
                    .setMessage("La reserva de llibres encara no està operativa. Aquesta funció s'activarà pròximament.")
                    .setPositiveButton("D'acord", null)
                    .show();

            // PENDENT quan el backend estigui actiu
            /*
            String today = LocalDate.now().toString();
            String due = LocalDate.now().plusMonths(1).toString();

            LoanRequest request = new LoanRequest(
                today,
                due,
                null, // No retornat encara
                userId,
                book.getId(),
                2 // Pendent
            );

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            Call<Void> call = api.reserveBook("Bearer " + token, request);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Reserva creada!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error creant la reserva", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error de connexió", Toast.LENGTH_SHORT).show();
                }
            });
            */
        }
    }
}
