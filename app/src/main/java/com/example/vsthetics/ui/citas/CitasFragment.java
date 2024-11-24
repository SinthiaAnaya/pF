package com.example.vsthetics.ui.citas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CitasFragment extends Fragment {

    private CitasViewModel citasViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_citas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("CitasFragment", "Fragmento de citas cargado");

        citasViewModel = new ViewModelProvider(this).get(CitasViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCitas);
        if (recyclerView == null) {
            Log.e("CitasFragment", "RecyclerView no encontrado en el layout");
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CitasAdapter adapter = new CitasAdapter();
        recyclerView.setAdapter(adapter);

        citasViewModel.getCitas().observe(getViewLifecycleOwner(), citas -> {
            Log.d("CitasFragment", "Citas actualizadas: " + citas.size());
            adapter.setCitas(citas);
        });

        Button btnAgregarCita = view.findViewById(R.id.btnAgregarCita);
        if (btnAgregarCita != null) {
            btnAgregarCita.setOnClickListener(v -> {
                Log.d("CitasFragment", "Botón agregar cita presionado");
                Citas nuevaCita = new Citas(null, "Cliente Prueba", "2024-11-23", "10:00", "Corte de cabello");
                citasViewModel.agregarCita(nuevaCita);
            });
        } else {
            Log.e("CitasFragment", "Botón agregar cita no encontrado");
        }
    }

}
