package com.example.vsthetics.ui.citas;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.Model.Servicios;
import com.example.vsthetics.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgregarCitaDialog extends DialogFragment {

    public interface OnCitaAgregadaListener {
        void onCitaAgregada(Citas cita);
    }

    private OnCitaAgregadaListener listener;
    private ActivityResultLauncher<Intent> galleryLauncher;

    public static AgregarCitaDialog newInstance(String tipoUsuario, String servicioId) {
        AgregarCitaDialog dialog = new AgregarCitaDialog();
        Bundle args = new Bundle();
        args.putString("tipo_usuario", tipoUsuario); // "admin" o "cliente"
        args.putString("servicio_id", servicioId); // ID del servicio seleccionado (para cliente)
        dialog.setArguments(args);
        return dialog;
    }

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
        Spinner spServicio = view.findViewById(R.id.spTipoServicio);
        ImageView ivFoto = view.findViewById(R.id.citaImagen);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.cita_estados, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapter);
        spEstado.setSelection(1); //Pendiente como default
        // Consultar los servicios desde Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("servicios") // Reemplaza "servicios" con el nombre de tu colección
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> servicios = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Asegúrate de que la clave "nombre" es la que contiene el nombre del servicio
                            String servicioNombre = document.getString("nombre");
                            if (servicioNombre != null) {
                                servicios.add(servicioNombre);
                            }
                        }// Obtener la lista de servicios de la base de datos
                        List<Servicios> servicio = new ArrayList<>();
                        // Poblamos el Spinner con los datos obtenidos de Firestore
                        ArrayAdapter<Servicios> servicioAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, servicio);
                        servicioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spServicio.setAdapter(servicioAdapter);
                    } else {
                        // En caso de error al obtener los datos
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
        // Verificar si es cliente o administrador
        String tipoUsuario = getArguments() != null ? getArguments().getString("tipo_usuario") : "admin";
        String servicioId = getArguments() != null ? getArguments().getString("servicio_id") : null;

        if ("cliente".equals(tipoUsuario)) {
            // Obtener datos del cliente y servicio desde la base de datos
            cargarDatosClienteYServicio(etCliente, etDescripcion, servicioId,spServicio,spEstado);

            // Deshabilitar edición de ciertos campos
            etCliente.setEnabled(false);
            etDescripcion.setEnabled(false);
        }

        ivFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        // Configura el lanzador
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                            ivFoto.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
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
            String servicio = spServicio.getSelectedItem().toString();

            BitmapDrawable drawable = (BitmapDrawable) ivFoto.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            // Convertir el Bitmap a Base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            String foto = Base64.encodeToString(byteArray, Base64.DEFAULT);

            if (TextUtils.isEmpty(cliente) || TextUtils.isEmpty(fecha) || TextUtils.isEmpty(hora) || TextUtils.isEmpty(descripcion)) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onCitaAgregada(new Citas(null, cliente, fecha, hora, descripcion, estado, servicio, foto));
            }
            dismiss(); // Cierra el diálogo
        });

        btnCancelar.setOnClickListener(v -> dismiss());
    }
    private void cargarDatosClienteYServicio(EditText etCliente, EditText etDescripcion, String servicioId, Spinner spServicio, Spinner spEstado) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Obtener datos del cliente
        String clienteId = auth.getCurrentUser().getUid();
        DocumentReference clienteDoc = firestore.collection("Usuarios").document(clienteId);

        clienteDoc.get().addOnCompleteListener(taskCliente -> {
            if (taskCliente.isSuccessful() && taskCliente.getResult() != null) {
                DocumentSnapshot clienteSnapshot = taskCliente.getResult();
                String nombreCliente = clienteSnapshot.getString("nombre");

                if ("0".equals(servicioId) || servicioId == null) {
                    firestore.collection("Servicios")
                            .get()
                            .addOnCompleteListener(taskServicios -> {
                                if (taskServicios.isSuccessful() && taskServicios.getResult() != null) {
                                    List<String> serviciosList = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : taskServicios.getResult()) {
                                        String nombreServicio = document.getString("nombre");
                                        if (nombreServicio != null) {
                                            serviciosList.add(nombreServicio);
                                        }
                                    }
                                    ArrayAdapter<String> servicioAdapter = new ArrayAdapter<>(requireContext(),
                                            android.R.layout.simple_spinner_item, serviciosList);
                                    servicioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spServicio.setAdapter(servicioAdapter);

                                    requireActivity().runOnUiThread(() -> {
                                        etCliente.setEnabled(true);
                                        etDescripcion.setEnabled(true);
                                    });
                                } else {
                                    Log.e("Firestore", "Error al obtener los servicios: " + taskServicios.getException());
                                }
                            });
                } else {
                    DocumentReference servicioDoc = firestore.collection("Servicios").document(servicioId);
                    servicioDoc.get().addOnCompleteListener(taskServicio -> {
                        if (taskServicio.isSuccessful() && taskServicio.getResult() != null) {
                            DocumentSnapshot servicioSnapshot = taskServicio.getResult();
                            String descripcionServicio = servicioSnapshot.getString("descripcion");
                            String nombreServicio = servicioSnapshot.getString("nombre");

                            List<String> serviciosList = new ArrayList<>();
                            serviciosList.add(nombreServicio);

                            ArrayAdapter<String> servicioAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, serviciosList);
                            servicioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spServicio.setAdapter(servicioAdapter);

                            requireActivity().runOnUiThread(() -> {
                                etCliente.setText(nombreCliente);
                                etDescripcion.setText(nombreServicio + " : " + descripcionServicio);
                                spEstado.setVisibility(View.GONE);
                            });
                        } else {
                            Log.e("Firestore", "Error al obtener los datos del servicio: " + taskServicio.getException());
                        }
                    });
                }
            } else {
                Log.e("Firestore", "Error al obtener los datos del cliente: " + taskCliente.getException());
            }
        });
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

