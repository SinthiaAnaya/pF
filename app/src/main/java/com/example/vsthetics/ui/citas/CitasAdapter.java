package com.example.vsthetics.ui.citas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.R;

import java.util.ArrayList;
import java.util.List;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitasViewHolder> {

    private List<Citas> citas = new ArrayList<>();
    private OnCitaClickListener listener;





    private Context context; // Contexto para operaciones específicas

    public CitasAdapter(Context context) {
        this.context = context; // Guardar el contexto si es necesario
    }


    // Método para establecer la lista de citas
    public void setCitas(List<Citas> citas) {
        this.citas = citas;
        notifyDataSetChanged(); // Notificar cambios en la lista
    }

    // Método para establecer el listener
    public void setOnCitaClickListener(OnCitaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CitasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasViewHolder holder, int position) {
        Citas cita = citas.get(position);

        // Configurar datos de la cita
        holder.tvCliente.setText(cita.getCliente());
        holder.tvFecha.setText(cita.getFecha());
        holder.tvHora.setText(cita.getHora());
        holder.tvEstado.setText(cita.getEstado());

        // Configurar clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCitaClick(cita); // Notificar al listener
            }
        });

        // Configurar clic para eliminar
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onCitaEliminar(cita); // Notificar al listener de eliminación
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    // Interfaz para manejar eventos de clic
    public interface OnCitaClickListener {
        void onCitaClick(Citas cita); // Abrir detalles
        void onCitaEliminar(Citas cita); // Eliminar cita
    }

    // Clase interna ViewHolder
    static class CitasViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvFecha, tvHora, tvEstado;

        public CitasViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asignar las vistas
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}

