package com.example.vsthetics.Model;

public class Citas {

        private String uid;
        private String cliente;
        private String fecha;
        private String hora;
        private String servicio;

        // Constructor vacío
        public Citas() {}

        // Getters y Setters

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    @Override
        public String toString() {
            return cliente + " - " + fecha + " - " + hora + " - " + servicio;
        }
    }


