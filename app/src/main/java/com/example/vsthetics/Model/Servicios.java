package com.example.vsthetics.Model;



import java.io.Serializable;

public class Servicios implements Serializable {

    private String uid;            // Identificador único del servicio
    private String nombre;         // Nombre del servicio
    private String descripcion;    // Descripción del servicio
    private double precio;         // Precio del servicio
    private int duracion;          // Duración del servicio en minutos

    // Constructor vacío necesario para Firestore y otras operaciones
    public Servicios() {}

    // Constructor con parámetros
    public Servicios(String uid, String nombre, String descripcion, double precio, int duracion) {
        this.uid = uid;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracion = duracion;
    }

    // Getters y setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    // Método toString() para representar el objeto como una cadena
    @Override
    public String toString() {
        return "Servicio{" +
                "uid='" + uid + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", duracion=" + duracion +
                '}';
    }
}

