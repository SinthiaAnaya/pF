package com.example.vsthetics.ui.pagos;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vsthetics.Model.Citas;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.Model.Pagos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PagosViewModel extends ViewModel {
    private MutableLiveData<List<Pagos>> pagosLiveData = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Pagos>> getPagos() {
        return pagosLiveData;
    }

    public void cargarPagosDesdeFirestore() {
        db.collection("pagos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Pagos> listaPagos = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Pagos pago = document.toObject(Pagos.class);
                    pago.setId(document.getId());
                    listaPagos.add(pago);
                }
                pagosLiveData.setValue(listaPagos);
            } else {
                Log.e("PagoViewModel", "Error al cargar pagos", task.getException());
            }
        });
    }

    public void agregarPago(Pagos pago) {
        db.collection("pagos").add(pago).addOnSuccessListener(documentReference -> {
            pago.setId(documentReference.getId());
            cargarPagosDesdeFirestore();
        });
    }

    public void eliminarPago(String pagoId) {
        db.collection("pagos").document(pagoId).delete().addOnSuccessListener(aVoid -> cargarPagosDesdeFirestore());
    }
}




