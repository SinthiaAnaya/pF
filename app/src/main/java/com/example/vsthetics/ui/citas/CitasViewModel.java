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
        cargarCitasDesdeFirestore();
    }

    public LiveData<List<Citas>> getCitas() {
        return citasList;
    }

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public void cargarCitasDesdeFirestore() {
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
                    citasList.setValue(citas); // Notifica a todos los observadores
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    Log.e("FirestoreError", "Error al cargar citas", e);
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

    public void actualizarCitaEnFirestore(String id, Citas cita) {
        db.collection("Citas").document(id)
                .set(cita)
                .addOnSuccessListener(aVoid -> cargarCitasDesdeFirestore())
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error al actualizar cita", e));
    }

    public void eliminarCita(String id) {
        db.collection("Citas").document(id) // Referencia al documento por su ID
                .delete() // Operación de eliminación
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Cita eliminada exitosamente.");
                    cargarCitasDesdeFirestore(); // Recargar las citas después de eliminar
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error al eliminar cita", e);
                });
    }
    public void eliminarCitaEnFirestore(String id) {
        db.collection("Citas").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    List<Citas> currentList = citasList.getValue();
                    if (currentList != null) {
                        currentList.removeIf(cita -> cita.getId().equals(id));
                        citasList.setValue(currentList); // Notificar cambios
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error al eliminar cita", e));
    }



    public void actualizarCita(String id, Citas cita) {
        db.collection("Citas").document(id)
                .set(cita)
                .addOnSuccessListener(aVoid -> cargarCitasDesdeFirestore())
                .addOnFailureListener(e -> {
                    // Manejo de errores
                });
    }


}
