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

/**
 * Adaptador per mostrar una llista de llibres en un RecyclerView.
 * Gestiona la lògica associada a cada tipus d'usuari (admin o lector) i
 * permet accions com veure detalls, editar, esborrar o reservar llibres.
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private List<Book> llibres;
    private int roleId;
    private String token;
    private Context context;


    /**
     * Constructor que rep la llista de llibres i les dades de sessió.
     *
     * @param llibres Llibres a mostrar
     * @param roleId  ID del rol de l'usuari (1 = admin)
     * @param token   JWT per a la comunicació amb l'API
     */
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

    /**
     * Substitueix la llista actual de llibres i refresca la vista.
     *
     * @param newBooks Nova llista de llibres
     */
    public void updateBooks(List<Book> newBooks) {
        this.llibres.clear();
        this.llibres.addAll(newBooks);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder per representar cada llibre.
     */
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

        /**
         * Associa les dades del llibre amb els elements visuals,
         * i configura els botons segons el rol de l'usuari.
         *
         * @param llibre Objecte llibre a mostrar
         */
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

            btnViewDetail.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), BookDetailActivity.class);
                intent.putExtra("BOOK_ID", llibre.getId());
                itemView.getContext().startActivity(intent);
            });

            if (roleId == 1) {
                // Administrador: pot editar i eliminar
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnReserve.setVisibility(View.GONE);

                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(context, EditBookActivity.class);
                    intent.putExtra("book_id", llibre.getId());
                    context.startActivity(intent);
                });

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
                // Usuari lector: pot reservar si està disponible
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                btnReserve.setVisibility(View.VISIBLE);

                if (llibre.getAvailable()) {
                    btnReserve.setEnabled(true);
                    btnReserve.setText("Reservar");
                    btnReserve.setOnClickListener(v -> reserveBook(llibre, getAdapterPosition()));
                } else {
                    btnReserve.setEnabled(false);
                    btnReserve.setText("Reservat");
                }
            }
        }

        /**
         * Crida a l'API per eliminar un llibre i actualitza la llista.
         *
         * @param bookId   ID del llibre a esborrar
         * @param position Posició del llibre a la llista
         */
        private void deleteBook(int bookId, int position) {
            SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);

            //ApiService api = RetrofitClient.getClient().create(ApiService.class);
            ApiService api = RetrofitClient.getClient(context).create(ApiService.class); //

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

        /**
         * Mostra un diàleg de confirmació i fa la reserva del llibre.
         *
         * @param llibre   Llibre a reservar
         * @param position Posició del llibre a la llista
         */
        private void reserveBook(Book llibre, int position) {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar reserva")
                    .setMessage("Vols reservar aquest llibre?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        Book updatedBook = new Book();
                        updatedBook.setId(llibre.getId());
                        updatedBook.setTitle(llibre.getTitle());
                        updatedBook.setIsbn(llibre.getIsbn());
                        updatedBook.setPublished_date(llibre.getPublished_date());
                        updatedBook.setAvailable(false);
                        updatedBook.setAuthor(llibre.getAuthor());
                        updatedBook.setGenre(llibre.getGenre());

                        //ApiService api = RetrofitClient.getClient().create(ApiService.class);
                        ApiService api = RetrofitClient.getClient(context).create(ApiService.class); // ✅

                        Call<Void> call = api.updateBook("Bearer " + token, llibre.getId(), updatedBook);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    showDialog("Llibre reservat correctament");
                                    llibre.setAvailable(false);
                                    notifyItemChanged(position);
                                } else {
                                    showDialog("No s'ha pogut reservar el llibre");
                                    Log.e("ReserveBook", "Error: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                showDialog("Error de connexió reservant llibre");
                                Log.e("ReserveBook", "Error de connexió", t);
                            }
                        });
                    })
                    .setNegativeButton("Cancel·la", null)
                    .show();
        }

        /**
         * Mostra un missatge informatiu en un diàleg simple.
         *
         * @param missatge Missatge a mostrar
         */
        private void showDialog(String missatge) {
            new AlertDialog.Builder(context)
                    .setTitle("Informació")
                    .setMessage(missatge)
                    .setPositiveButton("Tancar", null)
                    .show();
        }
    }
}
