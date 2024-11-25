package com.example.vsthetics.ui.citas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
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
        Spinner spEstado = view.findViewById(R.id.spEstado);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.cita_estados, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapter);
        spEstado.setSelection(1); //Pendiente como default

        // Mostrar el selector de fecha cuando el EditText de fecha se toque
        etFecha.setOnClickListener(v -> mostrarSelectorFecha(etFecha));

        // Mostrar el selector de hora cuando el EditText de hora se toque
        etHora.setOnClickListener(v -> mostrarSelectorHora(etHora));
        btnGuardar.setOnClickListener(v -> {
            String cliente = etCliente.getText().toString();
            String fecha = etFecha.getText().toString();
            String hora = etHora.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            String estado = spEstado.getSelectedItem().toString();

            if (TextUtils.isEmpty(cliente) || TextUtils.isEmpty(fecha) || TextUtils.isEmpty(hora) || TextUtils.isEmpty(descripcion)) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onCitaAgregada(new Citas(null, cliente, fecha, hora, descripcion, estado));
            }
            dismiss(); // Cierra el diálogo
        });

        btnCancelar.setOnClickListener(v -> dismiss());
    }
    private void mostrarSelectorFecha(EditText editTextFecha) {
        Calendar calendar = Calendar.getInstance();
        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    editTextFecha.setText(fechaSeleccionada);
                }, año, mes, dia);
        datePickerDialog.show();
    }

    // Método para mostrar el selector de hora
    private void mostrarSelectorHora(EditText editTextHora) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                    editTextHora.setText(horaSeleccionada);
                }, hora, minuto, true);
        timePickerDialog.show();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }
}

