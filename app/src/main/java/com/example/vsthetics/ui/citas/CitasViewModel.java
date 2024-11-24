package com.example.vsthetics.ui.citas;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vsthetics.Model.Citas;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CitasViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Citas>> citasList = new MutableLiveData<>();

    public CitasViewModel() {
        cargarCitas();
    }

    public LiveData<List<Citas>> getCitas() {
        return citasList;
    }

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private void cargarCitas() {
        isLoading.setValue(true);
        db.collection("Citas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Citas> citas = new ArrayList<>();
                    queryDocumentSnapshots.forEach(document -> {
                        Citas cita = document.toObject(Citas.class);
                        cita.setId(document.getId());
                        citas.add(cita);
                    });
                    citasList.setValue(citas);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    Log.e("FirestoreError", "Error al cargar citas", e);
                    // Manejo de errores
                });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public void agregarCita(Citas cita) {
        db.collection("Citas")
                .add(cita)
                .addOnSuccessListener(documentReference -> {
                    cita.setId(documentReference.getId());
                    List<Citas> currentList = citasList.getValue();
                    if (currentList == null) currentList = new ArrayList<>();
                    currentList.add(cita);
                    citasList.setValue(currentList);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error al cargar citas", e);
                    // Manejo de errores
                });
    }


    public void eliminarCita(String id) {
        db.collection("Citas").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> cargarCitas())
                .addOnFailureListener(e -> {
                    // Manejo de errores
                }).addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error al cargar citas", e);
                    // Agregar notificación al usuario
                });
        ;
    }
    public void actualizarCita(String id, Citas cita) {
        db.collection("Citas").document(id)
                .set(cita)
                .addOnSuccessListener(aVoid -> cargarCitas())
                .addOnFailureListener(e -> {
                    // Manejo de errores
                });
    }


}
