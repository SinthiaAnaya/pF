package com.example.vsthetics.ui.citas;

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

    public void setCitas(List<Citas> citas) {
        this.citas = citas;
        notifyDataSetChanged();
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
        holder.tvCliente.setText(cita.getCliente());
        holder.tvFecha.setText(cita.getFecha());
        holder.tvHora.setText(cita.getHora());
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class CitasViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvFecha, tvHora;

        public CitasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
        }
    }
}
