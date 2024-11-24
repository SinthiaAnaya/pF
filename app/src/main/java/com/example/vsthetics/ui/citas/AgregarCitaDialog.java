package com.example.vsthetics.ui.citas;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.R;

public class AgregarCitaDialog extends DialogFragment {

    public interface OnCitaAgregadaListener {
        void onCitaAgregada(Citas cita);
    }

    private OnCitaAgregadaListener listener;

    public void setOnCitaAgregadaListener(OnCitaAgregadaListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agregar_cita, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etCliente = view.findViewById(R.id.etCliente);
        EditText etFecha = view.findViewById(R.id.etFecha);
        EditText etHora = view.findViewById(R.id.etHora);
        EditText etDescripcion = view.findViewById(R.id.etDescripcion);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        btnGuardar.setOnClickListener(v -> {
            String cliente = etCliente.getText().toString();
            String fecha = etFecha.getText().toString();
            String hora = etHora.getText().toString();
            String descripcion = etDescripcion.getText().toString();

            if (TextUtils.isEmpty(cliente) || TextUtils.isEmpty(fecha) || TextUtils.isEmpty(hora) || TextUtils.isEmpty(descripcion)) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onCitaAgregada(new Citas(null, cliente, fecha, hora, descripcion));
            }
            dismiss(); // Cierra el diálogo
        });

        btnCancelar.setOnClickListener(v -> dismiss());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }
}

