package com.example.vsthetics.Model;

public class Pagos {
    private String id;          // Identificador único del pago
    private String usuarioId;   // ID del usuario asociado
    private String metodoPago;  // Método de pago (Ejemplo: "Tarjeta", "Efectivo")
    private double monto;       // Monto del pago
    private String fechaPago;   // Fecha del pago en formato String (Ejemplo: "2024-11-25")
    private String estado;      // Estado del pago (Ejemplo: "Completado", "Pendiente")

    // Constructor vacío requerido por Firestore
    public Pagos() {}

    // Constructor con todos los campos
    public Pagos(String id, String usuarioId, String metodoPago, double monto, String fechaPago, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
