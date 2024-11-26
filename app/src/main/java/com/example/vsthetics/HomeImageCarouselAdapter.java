package com.example.vsthetics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.Model.Servicios;
import com.example.vsthetics.ui.home.HomeFragment;

import java.util.List;

public class HomeImageCarouselAdapter extends RecyclerView.Adapter<HomeImageCarouselAdapter.ViewHolder> {
    HomeFragment context;
    private List<Servicios> serviciosList;
    OnItemClickListener onItemClickListener;

    public HomeImageCarouselAdapter(HomeFragment context, List<Servicios> serviciosList) {
        this.context = context;
        this.serviciosList = serviciosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getContext()).inflate(R.layout.image_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicios servicio = serviciosList.get(position);
        String base64Image = servicio.getFoto();
//        System.out.println("la imagen\n"+base64Image);
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageView.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                Log.e("ImageDecodeError", "Fallo decodificar la imagen", e);
                holder.imageView.setImageResource(0);
            }
        } else {
            holder.imageView.setImageResource(0); //en blanco
        }
        holder.tvServTitulo.setText(
                servicio.getNombre() != null ? servicio.getNombre() : "Nombre no disponible"
        );
        holder.tvServSub.setText(
                servicio.getDescripcion() != null ? servicio.getDescripcion() : "Descripción no disponible"
        );

        holder.tvServPrecio.setText("$"+
                (String.valueOf(servicio.getPrecio()) != null ? String.valueOf(servicio.getPrecio()) : "")
        );

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(holder.imageView, servicio);

            }
        });
    }



    @Override
    public int getItemCount() {
        return serviciosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvServTitulo, tvServSub, tvServPrecio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServTitulo = itemView.findViewById(R.id.tvServicioTitulo);
            tvServSub = itemView.findViewById(R.id.tvServicioSubtitulo);
            imageView = itemView.findViewById(R.id.list_item_image);
            tvServPrecio = itemView.findViewById(R.id.tvServicioPrecio);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(ImageView imageView, Servicios servicio);
    }
}