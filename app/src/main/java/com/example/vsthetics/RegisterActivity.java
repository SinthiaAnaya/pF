package com.example.vsthetics;



import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase Authentication y Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Vincular elementos de la interfaz
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        Button registerButton = findViewById(R.id.registerButton);

        // Configuración del botón de registro
        registerButton.setOnClickListener(v -> {
            String nombre = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Registrar usuario con Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Guardar datos del usuario en Firestore
                                guardarUsuarioEnFirestore(user.getUid(), nombre, email);
                            }
                        } else {
                            Toast.makeText(this, "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void guardarUsuarioEnFirestore(String userId, String nombre, String email) {
        // Crear un mapa con los datos del usuario
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("email", email);

        // Guardar datos en Firestore en la colección "Usuarios"
        firestore.collection("Usuarios").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish(); // Cerrar actividad después del registro
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

