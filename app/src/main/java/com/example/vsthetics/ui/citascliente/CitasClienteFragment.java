package com.example.vsthetics.ui.citascliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.vsthetics.DetallesCitaActivity;
import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.R;
import com.example.vsthetics.ui.citas.AgregarCitaDialog;
import com.example.vsthetics.ui.citas.CitasAdapter;
import com.example.vsthetics.ui.citas.CitasViewModel;


public class CitasClienteFragment extends Fragment {

    CitasViewModel citasViewModel;
    public CitasClienteFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_citas_cliente, container, false);

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("CitasFragment", "recargando citas");

        citasViewModel.cargarCitasDeCliente();
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
        CitasClienteAdapter adapter = new CitasClienteAdapter(getContext());
        recyclerView.setAdapter(adapter);

        citasViewModel.getCitas().observe(getViewLifecycleOwner(), citas -> {
            adapter.setCitas(citas); // Actualizar la lista del RecyclerView
        });

        // Cargar las citas inicialmente
        citasViewModel.cargarCitasDeCliente();



//        Button btnAgregarCita = view.findViewById(R.id.btnAgregarCita);
//        btnAgregarCita.setOnClickListener(v -> {
//            AgregarCitaDialog dialog = new AgregarCitaDialog();
//            dialog.setOnCitaAgregadaListener(cita -> {
//                Log.d("CitasFragment", "Nueva cita agregada: " + cita);
//                citasViewModel.agregarCita(cita);
//                Toast.makeText(getContext(), "Cita agregada", Toast.LENGTH_SHORT).show();
//            });
//            dialog.show(getParentFragmentManager(), "AgregarCitaDialog");
//        });

        citasViewModel.getCitas().observe(getViewLifecycleOwner(), citas -> {
            adapter.setCitas(citas);
        });

//        // Configurar el Listener en el Adaptador
//        adapter.setOnCitaClickListener(new CitasClienteAdapter.OnCitaClickListener() {
//            @Override
//            public void onCitaClick(Citas cita) {
//                // Abrir la actividad de detalles
//                Intent intent = new Intent(getContext(), DetallesCitaActivity.class);
//                intent.putExtra("cita", cita); // Pasar la cita seleccionada
//                startActivity(intent);
//            }
//
//            @Override
//            public void onCitaEliminar(Citas cita) {
//                mostrarConfirmacionEliminar(cita); // Implementación para eliminar
//                citasViewModel.cargarCitasDesdeFirestore();
//            }
//        });
    }

    private void mostrarConfirmacionEliminar(Citas cita) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Cita")
                .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    citasViewModel.eliminarCita(cita.getId());
                    Toast.makeText(getContext(), "Cita eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}