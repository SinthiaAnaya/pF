package com.example.vsthetics.ui.pagos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.Model.Pagos;
import com.example.vsthetics.Model.Servicios;
import com.example.vsthetics.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagoDialog extends DialogFragment {
    public interface OnPagoAgregadoListener {
        void onPagoAgregado(Pagos pago);
    }
    private List<String> listaCitaIds = new ArrayList<>();

    private OnPagoAgregadoListener listener;

    public void setOnPagoAgregadoListener(OnPagoAgregadoListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_agregar_pago, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spCita = view.findViewById(R.id.spCita);
        EditText etMonto = view.findViewById(R.id.etMonto);
        Spinner spMetodoPago = view.findViewById(R.id.spMetodoPago);
        Spinner spEstado = view.findViewById(R.id.spEstado);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        // Cargar IDs de citas en el Spinner
        cargarCitas(spCita);

        // Configurar Spinner de Métodos de Pago
        ArrayAdapter<CharSequence> metodoPagoAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.metodos_pago, android.R.layout.simple_spinner_item);
        metodoPagoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(metodoPagoAdapter);

        // Configurar Spinner de Estados
        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.estado_pagos, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);

        // Guardar pago
        btnGuardar.setOnClickListener(v -> {
            String citaId = spCita.getSelectedItem().toString(); // ID de la cita seleccionada
            String metodoPago = spMetodoPago.getSelectedItem().toString();
            String estado = spEstado.getSelectedItem().toString();
            double monto;

            try {
                monto = Double.parseDouble(etMonto.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Por favor ingresa un monto válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar el pago
            guardarPago(citaId, metodoPago, monto, estado);
            dismiss();
        });

        btnCancelar.setOnClickListener(v -> dismiss());
    }

    private void guardarPago(String citaId, String metodoPago, double monto, String estado) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Crear objeto Pago
        Map<String, Object> pago = new HashMap<>();
        pago.put("citaId", citaId); // Asociar con el ID de la cita seleccionada
        pago.put("metodoPago", metodoPago);
        pago.put("monto", monto);
        pago.put("estado", estado);
        pago.put("fecha", new Date().toString()); // Fecha actual

        // Guardar en la colección "pagos"
        firestore.collection("pagos")
                .add(pago)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Pago registrado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al guardar el pago: " + e.getMessage());
                });
    }



    private void cargarCitas(Spinner spCita) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("citas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> citaIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            citaIds.add(document.getId()); // Obtén los IDs de las citas
                        }

                        if (!citaIds.isEmpty()) {
                            ArrayAdapter<String> adapterCitas = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, citaIds);
                            adapterCitas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spCita.setAdapter(adapterCitas);
                        } else {
                            Toast.makeText(getContext(), "No hay citas disponibles", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Error al cargar citas", task.getException());
                        Toast.makeText(getContext(), "Error al cargar citas", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void cargarDatosCitaYPago(EditText etCliente, EditText etMonto, Spinner spCita, Spinner spMetodoPago, Spinner spEstado) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Cargar citas desde Firestore
        firestore.collection("citas")
                .get()
                .addOnCompleteListener(taskCitas -> {
                    if (taskCitas.isSuccessful() && taskCitas.getResult() != null) {
                        List<String> citasList = new ArrayList<>();
                        List<String> citasIds = new ArrayList<>(); // Para almacenar los IDs de las citas

                        for (QueryDocumentSnapshot document : taskCitas.getResult()) {
                            String citaId = document.getId();
                            String cliente = document.getString("cliente");
                            String servicio = document.getString("servicio");
                            String fecha = document.getString("fecha");

                            // Combina cliente, servicio y fecha para mostrar en el Spinner
                            String citaInfo = "Cliente: " + cliente + " - Servicio: " + servicio + " - Fecha: " + fecha;
                            citasList.add(citaInfo);
                            citasIds.add(citaId); // Agregar ID correspondiente
                        }

                        // Configurar el Spinner con las citas
                        ArrayAdapter<String> citasAdapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, citasList);
                        citasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCita.setAdapter(citasAdapter);

                        // Manejar selección en el Spinner
                        spCita.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedCitaId = citasIds.get(position); // Obtén el ID de la cita seleccionada
                                obtenerClientePorCita(selectedCitaId, etCliente);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                    } else {
                        Log.e("Firestore", "Error al cargar citas: " + taskCitas.getException());
                    }
                });

        // Configurar Spinner de Métodos de Pago
        ArrayAdapter<CharSequence> metodoPagoAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.metodos_pago, android.R.layout.simple_spinner_item);
        metodoPagoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(metodoPagoAdapter);

        // Configurar Spinner de Estados de Pago
        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.estado_pagos, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);
    }

    // Método para obtener el cliente asociado a una cita
    private void obtenerClientePorCita(String citaId, EditText etCliente) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("citas").document(citaId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot citaSnapshot = task.getResult();
                        String cliente = citaSnapshot.getString("cliente");
                        requireActivity().runOnUiThread(() -> etCliente.setText(cliente)); // Actualiza el campo de cliente
                    } else {
                        Log.e("Firestore", "Error al obtener cliente de la cita: " + task.getException());
                    }
                });
    }

}

