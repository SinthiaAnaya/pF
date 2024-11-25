package com.example.vsthetics.ui.citascliente;

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

public class CitasClienteAdapter extends RecyclerView.Adapter<CitasClienteAdapter.CitasViewHolder> {

    private List<Citas> citas = new ArrayList<>();
    private OnCitaClickListener listener;

    public CitasClienteAdapter(Context context) {
    }

    public void setCitas(List<Citas> citas) {
        this.citas = citas;
        notifyDataSetChanged();
    }

    public void setOnCitaClickListener(OnCitaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CitasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita_cliente, parent, false);
        return new CitasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasViewHolder holder, int position) {
        Citas cita = citas.get(position);
        holder.tvDescripcion.setText(cita.getDescripcion());
        holder.tvFecha.setText(cita.getFecha());
        holder.tvHora.setText(cita.getHora());
        holder.tvEstado.setText(cita.getEstado());

        // Configurar clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCitaClick(cita); // Notificar al listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    public interface OnCitaClickListener {
        void onCitaClick(Citas cita);

        void onCitaEliminar(Citas cita);
    }

    static class CitasViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcion, tvFecha, tvHora, tvEstado;

        public CitasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
