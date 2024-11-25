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
import com.example.vsthetics.Model.Servicios;
import com.example.vsthetics.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentHomeBinding binding;
    private HomeImageCarouselAdapter adapter;
    private List<Servicios> serviciosList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView recyclerView = binding.recycler;
        adapter = new HomeImageCarouselAdapter(HomeFragment.this, serviciosList);

        cargarServicios(recyclerView, adapter);


//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
                                    if (servicio.getNombre() == null) { // datos temporales si el Servicio todavia no se termina de agregar
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


                            }catch (Exception ex){

                            }

                        }
                        adapter.setOnItemClickListener((imageView, servicio) -> {
                            Log.d("RecyclerView", "Clic item: " + servicio.getUid());
                        });
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

