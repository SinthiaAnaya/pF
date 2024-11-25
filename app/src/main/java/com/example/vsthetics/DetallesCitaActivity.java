package com.example.vsthetics;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.R;
import com.example.vsthetics.ui.citas.CitasViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetallesCitaActivity extends AppCompatActivity {

    private EditText etCliente, etFecha, etHora, etDescripcion;
    private Button btnGuardar, btnEliminar;
    private Spinner spEstado;
    private final MutableLiveData<List<Citas>> citasList = new MutableLiveData<>();
    private Citas cita;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    CitasViewModel ca = new CitasViewModel();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CitasViewModel citasViewModel;

    @SuppressLint("MissingInflatedId")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cita);

        // Inicializar el ViewModel
        citasViewModel = new ViewModelProvider(this).get(CitasViewModel.class);

        // Configurar las vistas
        etCliente = findViewById(R.id.etCliente);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        spEstado = findViewById(R.id.spEstado);

        // Configurar los datos de la cita
        cita = (Citas) getIntent().getSerializableExtra("cita");
        if (cita != null) {
            etCliente.setText(cita.getCliente());
            etFecha.setText(cita.getFecha());
            etHora.setText(cita.getHora());
            etDescripcion.setText(cita.getDescripcion());
            String estado = cita.getEstado();  // Get the estado (state) as a string
            if (estado != null) {
                switch (estado) {
                    case "Cancelado":
                        spEstado.setSelection(0);
                        break;
                    case "Pendiente":
                        spEstado.setSelection(1);
                        break;
                    case "Agendado":
                        spEstado.setSelection(2);
                        break;
                    case "Completado":
                        spEstado.setSelection(3);
                        break;
                    default:
                        spEstado.setSelection(0);
                        break;
                }
            }
        }

        // Configurar botones
        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }
    private void eliminarCita(String id) {
        citasViewModel.eliminarCitaEnFirestore(id);
    }

    private void guardarCambios() {
        String nuevoCliente = etCliente.getText().toString().trim();
        String nuevaFecha = etFecha.getText().toString().trim();
        String nuevaHora = etHora.getText().toString().trim();
        String nuevaDescripcion = etDescripcion.getText().toString().trim();
        String nuevaEstado = spEstado.getSelectedItem().toString();

        if (nuevoCliente.isEmpty() || nuevaFecha.isEmpty() || nuevaHora.isEmpty() || nuevaDescripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        cita.setCliente(nuevoCliente);
        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        cita.setDescripcion(nuevaDescripcion);
        cita.setEstado(nuevaEstado);

        actualizarCita(cita.getId(), cita);
        CitasViewModel viewModel = new ViewModelProvider(this).get(CitasViewModel.class);


        citasViewModel.actualizarCitaEnFirestore(cita.getId(), cita);

        Toast.makeText(this, "Cita actualizada", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Cita")
                .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    citasViewModel.eliminarCitaEnFirestore(cita.getId());
                    Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
    public void actualizarCita(String id, Citas cita) {
        db.collection("Citas").document(id)
                .set(cita)
                .addOnSuccessListener(aVoid -> {
                    List<Citas> currentList = citasList.getValue();
                    if (currentList != null) {
                        for (int i = 0; i < currentList.size(); i++) {
                            if (currentList.get(i).getId().equals(id)) {
                                currentList.set(i, cita);
                                break;
                            }
                        }
                        citasList.setValue(currentList); // Actualizar la lista local
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error al actualizar cita", e));
    }

}