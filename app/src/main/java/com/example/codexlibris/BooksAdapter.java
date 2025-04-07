package com.example.codexlibris;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
                btnDelete.setOnClickListener(v ->
                        Toast.makeText(context, "Esborrar llibre: " + llibre.getTitle(), Toast.LENGTH_SHORT).show()
                );
            } else {
                // Usuari: es mostra reservar
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                btnReserve.setVisibility(View.VISIBLE);

                btnReserve.setOnClickListener(v ->
                        Toast.makeText(context, "Reservar llibre: " + llibre.getTitle(), Toast.LENGTH_SHORT).show()
                );
            }
        }
    }

}
