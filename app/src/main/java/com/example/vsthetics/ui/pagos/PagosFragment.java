package com.example.vsthetics.ui.pagos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.DetallesCitaActivity;
import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.Model.Pagos;
import com.example.vsthetics.R;
import com.example.vsthetics.ui.citas.AgregarCitaDialog;
import com.example.vsthetics.ui.citas.CitasAdapter;
import com.example.vsthetics.ui.citas.CitasViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class PagosFragment extends Fragment {
    private PagosViewModel pagoViewModel;
    private PagosAdapter pagoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pagos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerViewPagos = view.findViewById(R.id.recyclerViewPagos);
        FloatingActionButton btnAgregarPago = view.findViewById(R.id.btnAgregarPago);

        // Configurar RecyclerView
        recyclerViewPagos.setLayoutManager(new LinearLayoutManager(getContext()));
        pagoAdapter = new PagosAdapter(getContext(), new PagosAdapter.OnPagoClickListener() {
            @Override
            public void onPagoClick(Pagos pago) {
                mostrarDetallesPago(pago);
            }

            @Override
            public void onPagoEliminar(Pagos pagos) {
                mostrarConfirmacionEliminar(pagos);
            }
        });
        recyclerViewPagos.setAdapter(pagoAdapter);

        // Configurar ViewModel
        pagoViewModel = new ViewModelProvider(this).get(PagosViewModel.class);
        pagoViewModel.getPagos().observe(getViewLifecycleOwner(), pagos -> {
            pagoAdapter.setPagos(pagos);
        });

        // Cargar pagos desde Firestore
        pagoViewModel.cargarPagosDesdeFirestore();

        // Botón para agregar nuevo pago
        btnAgregarPago.setOnClickListener(v -> {
            PagoDialog dialog = new PagoDialog();
            dialog.setOnPagoAgregadoListener(nuevoPago -> {
                pagoViewModel.agregarPago(nuevoPago);
                Toast.makeText(getContext(), "Pago agregado correctamente", Toast.LENGTH_SHORT).show();
            });
            dialog.show(getParentFragmentManager(), "AgregarPagoDialog");
        });
    }

    private void mostrarDetallesPago(Pagos pago) {
        // Aquí puedes abrir una actividad o diálogo para mostrar detalles del pago
        new AlertDialog.Builder(requireContext())
                .setTitle("Detalles del Pago")
                .setMessage("Método: " + pago.getMetodoPago() +
                        "\nMonto: $" + pago.getMonto() +
                        "\nFecha: " + pago.getFechaPago() +
                        "\nEstado: " + pago.getEstado())
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void mostrarConfirmacionEliminar(Pagos pago) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Pago")
                .setMessage("¿Estás seguro de que deseas eliminar este pago?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    pagoViewModel.eliminarPago(pago.getId());
                    Toast.makeText(getContext(), "Pago eliminado correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
