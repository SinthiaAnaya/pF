package com.example.vsthetics.ui.citas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.DetallesCitaActivity;
import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.R;

import java.util.ArrayList;
import java.util.List;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitasViewHolder> {

    private List<Citas> citas = new ArrayList<>();
    private OnCitaClickListener listener;
    private Context context;
    private List<Citas> citasList;
    public CitasAdapter(Context context) {
        this.context = context;
    }

    public void setCitas(List<Citas> citas) {
        this.citas = citas;
        notifyDataSetChanged();
    }


    // Constructor del adaptador


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

        // Manejar clic en la tarjeta
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallesCitaActivity.class);
            intent.putExtra("cita", cita); // Enviar el objeto `Citas` a la actividad
            context.startActivity(intent);
        });

        // Configurar clic en el botón "Eliminar"
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCitaEliminar(cita); // Notificar al fragmento con la cita a eliminar
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
    // Configurar el Listener desde el Fragmento
    public void setOnCitaClickListener(OnCitaClickListener listener) {
        this.listener = listener;
    }

    static class CitasViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvFecha, tvHora;
        Button btnEditar, btnEliminar;

        public CitasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
