package com.example.vsthetics.Model;

import java.io.Serializable;

public class Usuarios implements Serializable {
    private String uid, nombre, correo, contra;

    public Usuarios() {
    }

    public Usuarios(String uid, String nombre, String correo, String contra) {
        this.uid = uid;
        this.nombre = nombre;
        this.correo = correo;
        this.contra = contra;
    }

    public Usuarios(String uid, String nombre, String correo) {//usuario opcional sin contrasena
        this.uid = uid;
        this.nombre = nombre;
        this.correo = correo;
    }

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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
