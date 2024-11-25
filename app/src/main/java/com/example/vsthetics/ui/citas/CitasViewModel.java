package com.example.vsthetics.ui.citas;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vsthetics.Model.Citas;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CitasViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Citas>> citasList = new MutableLiveData<>();
    private final MutableLiveData<List<Citas>> filteredCitasList = new MutableLiveData<>();
    public CitasViewModel() {
        cargarCitasDesdeFirestore();
        filteredCitasList.setValue(new ArrayList<>());
    }

    public LiveData<List<Citas>> getFilteredCitas() {
        return filteredCitasList;
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

    public void cargarCitasDeCliente() {
        isLoading.setValue(true);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("intentando buscar citas del uid: "+currentUserId);
        db.collection("Citas")
                .whereEqualTo("uid",currentUserId)
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

    public void filtrarCitasPorFecha(String fechaFiltro) {
        List<Citas> originalList = citasList.getValue();
        if(fechaFiltro != "desde el principio"){
            if (originalList != null) {
                List<Citas> filteredList = originalList.stream()
                        .filter(cita -> cita.getFecha().equals(fechaFiltro))
                        .collect(Collectors.toList());
                filteredCitasList.setValue(filteredList);
            }
        }else{
            resetFilter();
        }

    }

    // Filter method for estado
    public void filtrarCitasPorEstado(String estadoFiltro) {
        if (citasList.getValue() == null) return; // Early exit if the list is null

        if (!estadoFiltro.equals("Todo")) {
            List<Citas> originalList = citasList.getValue();
            List<Citas> filteredList = originalList.stream()
                    .filter(cita -> cita.getEstado().equals(estadoFiltro))
                    .collect(Collectors.toList());
            filteredCitasList.setValue(filteredList);
        } else {
            // Only update if it's not already the same
            if (!filteredCitasList.getValue().equals(citasList.getValue())) {
                filteredCitasList.setValue(citasList.getValue());
            }
        }
    }

    public void resetFilter() {
        filteredCitasList.setValue(citasList.getValue());
    }



}



