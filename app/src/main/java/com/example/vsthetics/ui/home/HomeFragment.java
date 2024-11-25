package com.example.vsthetics.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.HomeImageCarouselAdapter;
import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.Model.Servicios;
import com.example.vsthetics.databinding.FragmentHomeBinding;
import com.example.vsthetics.ui.citas.AgregarCitaDialog;
import com.example.vsthetics.ui.citas.CitasViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentHomeBinding binding;
    private HomeImageCarouselAdapter adapter;
    private List<Servicios> serviciosList = new ArrayList<>();
    private CitasViewModel citasViewModel; // Asegúrate de declarar el ViewModel aquí

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inicializa el ViewModel
        citasViewModel = new ViewModelProvider(this).get(CitasViewModel.class); // Obtener el ViewModel

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView recyclerView = binding.recycler;
        adapter = new HomeImageCarouselAdapter(HomeFragment.this, serviciosList);

        cargarServicios(recyclerView, adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void cargarServicios(RecyclerView recyclerView, HomeImageCarouselAdapter adapter) {
        db.collection("Servicios")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error al cargar servicios", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        serviciosList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                Servicios servicio = document.toObject(Servicios.class);
                                servicio.setUid(document.getId());
                                if (servicio != null) {
                                    // Valores predeterminados en caso de que falten datos
                                    if (servicio.getNombre() == null) {
                                        servicio.setNombre("Agregando...");
                                    }
                                    if (servicio.getDescripcion() == null) {
                                        servicio.setDescripcion("Por favor espere...");
                                    }
                                    if (servicio.getPrecio() == 0) {
                                        servicio.setPrecio(0.0);
                                    }
                                    if (servicio.getDuracion() == 0) {
                                        servicio.setDuracion(1);
                                    }
                                    serviciosList.add(servicio);
                                }
                            } catch (Exception ex) {
                                Log.e("FirestoreError", "Error al procesar el servicio", ex);
                            }
                        }

                        // Configuración del RecyclerView y manejo de clics en el adapter
                        adapter.setOnItemClickListener((imageView, servicio) -> {
                            Log.d("RecyclerView", "Clic item: " + servicio.getUid());

                            // Mostrar el AgregarCitaDialog cuando un item sea clickeado
                            AgregarCitaDialog dialog = AgregarCitaDialog.newInstance("cliente", servicio.getUid());
                            dialog.setOnCitaAgregadaListener(cita -> {
                                // Asegúrate de que el usuario esté autenticado
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                cita.setUid(userId); // Establece el UID del cliente en la cita

                                // Verifica que el ViewModel no sea null antes de agregar la cita
                                if (citasViewModel != null) {
                                    citasViewModel.agregarCita(cita); // Llama al método para agregar la cita
                                    // Muestra un mensaje de éxito
                                    Toast.makeText(getContext(), "Cita agregada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Error", "CitasViewModel no está inicializado");
                                }
                            });

                            // Mostrar el diálogo
                            dialog.show(getChildFragmentManager(), "AgregarCitaDialog");
                        });

                        // Configura el adapter y el RecyclerView
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
