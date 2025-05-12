package com.example.codexlibris;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adaptador per mostrar una llista d'objectes {@link LibroReservado} en un RecyclerView.
 * S'utilitza a la vista de l'historial de llibres llegits/reservats per un usuari.
 */
public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.LibroViewHolder> {

    private List<LibroReservado> llibres;

    /**
     * Constructor de l'adaptador.
     *
     * @param llibres Llista de llibres reservats que es mostraran
     */
    public HistorialAdapter(List<LibroReservado> llibres) {
        this.llibres = llibres;
    }

    @NonNull
    @Override
    public LibroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LibroViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull LibroViewHolder holder, int position) {
        LibroReservado llibre = llibres.get(position);
        holder.textView.setText(llibre.getTitle() + " — " + llibre.getAuthor());
    }

    @Override
    public int getItemCount() {
        return llibres.size();
    }

    /**
     * ViewHolder per mostrar un llibre reservat a la llista.
     */
    static class LibroViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView la vista que representa un ítem individual
         */
        public LibroViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
