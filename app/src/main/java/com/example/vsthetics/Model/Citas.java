package com.example.vsthetics.Model;

import java.io.Serializable;

public class Citas implements Serializable {
    private String id;
    private String cliente;
    private String fecha;
    private String hora;
    private String descripcion;

    private String estado;

    private String uid;

    // Constructor vacío necesario para Firebase
    public Citas() {}

    // Constructor con parámetros
    public Citas(String id, String cliente, String fecha, String hora, String descripcion, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    @Override
    public String toString() {
        return "Citas{" +
                "id='" + id + '\'' +
                ", cliente='" + cliente + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

