package com.example.vsthetics.ui.pagos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.Model.Pagos;
import com.example.vsthetics.R;

import java.util.ArrayList;
import java.util.List;

public class PagosAdapter extends RecyclerView.Adapter<PagosAdapter.PagoViewHolder> {
    private Context context;
    private List<Pagos> pagos = new ArrayList<>();
    private OnPagoClickListener listener;
    Button btnAgregar;

    public interface OnPagoClickListener {
        void onPagoClick(Pagos pago);
        void onPagoEliminar(Pagos pago);
    }

    public PagosAdapter(Context context, OnPagoClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setPagos(List<Pagos> pagos) {
        this.pagos = pagos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pago, parent, false);
        return new PagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int position) {
        Pagos pago = pagos.get(position);
        holder.bind(pago);
    }

    @Override
    public int getItemCount() {
        return pagos.size();
    }

    class PagoViewHolder extends RecyclerView.ViewHolder {
        TextView tvMetodoPago, tvMonto, tvFecha, tvEstado;
        Button btnEliminar;

        public PagoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMetodoPago = itemView.findViewById(R.id.tvMetodoPago);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(Pagos pago) {
            tvMetodoPago.setText(pago.getMetodoPago());
            tvMonto.setText(String.format("Monto: $%.2f", pago.getMonto()));
            tvFecha.setText(pago.getFechaPago());
            tvEstado.setText(pago.getEstado());

            itemView.setOnClickListener(v -> listener.onPagoClick(pago));
            btnEliminar.setOnClickListener(v -> listener.onPagoEliminar(pago));
        }
    }
}
