package com.example.vsthetics.ui.citas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vsthetics.DetallesCitaActivity;
import com.example.vsthetics.Model.Citas;
import com.example.vsthetics.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class CitasFragment extends Fragment {
    private Spinner spinnerFiltroFecha;
    private CitasViewModel citasViewModel;
    private CitasAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_citas, container, false);


    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("CitasFragment", "recargando citas");
        citasViewModel.cargarCitasDesdeFirestore();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        citasViewModel = new ViewModelProvider(this).get(CitasViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CitasAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Observador para actualizar la lista

        // Filtros
        Spinner spinnerFecha = view.findViewById(R.id.spinnerFiltroFecha);
        Spinner spinnerEstado = view.findViewById(R.id.spinnerFiltroEstado);
        spinnerEstado.setSelection(0);
        spinnerFecha.setSelection(0);

//        citasViewModel.filtrarCitasPorFecha("desde el principio");
        // Configurar listeners para los filtros
        spinnerFecha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItemPosition() > 0){
                    String fechaFiltro = parent.getItemAtPosition(position).toString();
                    citasViewModel.getFilteredCitas().observe(getViewLifecycleOwner(), citas -> {
                        adapter.setCitas(citas);  //MOSTRAR LISTA FILTRADA
                    });
                    citasViewModel.filtrarCitasPorFecha(fechaFiltro);
                    spinnerEstado.setSelection(0);
                }else{
                    citasViewModel.getCitas().observe(getViewLifecycleOwner(), citas -> {
                        adapter.setCitas(citas);  //MOSTRAR LISTA ORIGINAL
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItemPosition() > 0){
                    String estadoFiltro = parent.getItemAtPosition(position).toString();
                    citasViewModel.getFilteredCitas().observe(getViewLifecycleOwner(), citas -> {
                        adapter.setCitas(citas);  //MOSTRAR LISTA FILTRADA
                    });
                    citasViewModel.filtrarCitasPorEstado(estadoFiltro);
                    spinnerFecha.setSelection(0);
                }else{
                    citasViewModel.getCitas().observe(getViewLifecycleOwner(), citas -> {
                        adapter.setCitas(citas);  //MOSTRAR LISTA ORIGINAL
                    });
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Botón para agregar cita
        FloatingActionButton btnAgregarCita = view.findViewById(R.id.btnAgregarCita);
        btnAgregarCita.setOnClickListener(v -> {
            AgregarCitaDialog dialog = new AgregarCitaDialog();
            dialog.setOnCitaAgregadaListener(cita -> {
                cita.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                citasViewModel.agregarCita(cita);
                Toast.makeText(getContext(), "Cita agregada", Toast.LENGTH_SHORT).show();
            });
            dialog.show(getParentFragmentManager(), "AgregarCitaDialog");
        });

        // Listener en el adaptador para abrir detalles
        adapter.setOnCitaClickListener(new CitasAdapter.OnCitaClickListener() {
            @Override
            public void onCitaClick(Citas cita) {
                Intent intent = new Intent(getContext(), DetallesCitaActivity.class);
                intent.putExtra("cita", cita);
                startActivity(intent);
            }

            @Override
            public void onCitaEliminar(Citas cita) {
                mostrarConfirmacionEliminar(cita);
            }
        });


    }

    private void mostrarConfirmacionEliminar(Citas cita) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Cita")
                .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    citasViewModel.eliminarCita(cita.getId());
                    Toast.makeText(getContext(), "Cita eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


        }

