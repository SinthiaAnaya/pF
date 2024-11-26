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
import java.util.List;

public class PagoDialog extends DialogFragment {
    public interface OnPagoAgregadoListener {
        void onPagoAgregado(Pagos pago);
    }

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

        EditText etMonto = view.findViewById(R.id.etMontos);
        EditText etFechaPago = view.findViewById(R.id.etFechaPagos);
        Spinner spMetodoPago = view.findViewById(R.id.spMetodoPagos);
        Spinner spEstado = view.findViewById(R.id.spEstado);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        // Configurar Spinner de Método de Pago
        ArrayAdapter<CharSequence> adapterMetodoPago = ArrayAdapter.createFromResource(requireContext(),
                R.array.metodos_pago, android.R.layout.simple_spinner_item);
        adapterMetodoPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(adapterMetodoPago);

        // Configurar Spinner de Estado
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(requireContext(),
                R.array.estado_pagos, android.R.layout.simple_spinner_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterEstado);

        // Mostrar selector de fecha cuando se toque el EditText de fecha
        etFechaPago.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String fechaSeleccionada = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                        etFechaPago.setText(fechaSeleccionada);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Botón Guardar
        btnGuardar.setOnClickListener(v -> {
            String metodoPago = spMetodoPago.getSelectedItem().toString();
            String estado = spEstado.getSelectedItem().toString();
            String fechaPago = etFechaPago.getText().toString();
            double monto;

            try {
                monto = Double.parseDouble(etMonto.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Por favor ingresa un monto válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                Pagos nuevoPago = new Pagos(
                        null,
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        metodoPago,
                        monto,
                        fechaPago,
                        estado
                );
                listener.onPagoAgregado(nuevoPago);
                dismiss();
            }
        });

        // Botón Cancelar
        btnCancelar.setOnClickListener(v -> dismiss());
    }
}

