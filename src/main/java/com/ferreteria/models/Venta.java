
package com.ferreteria.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {

    private int numero;
    private LocalDateTime fecha;
    private List<String> productos;
    private double total;

    public Venta(int numero, List<String> productos, double total) {
        this.numero = numero;
        this.fecha = LocalDateTime.now();
        this.productos = new ArrayList<>(productos);
        this.total = total;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<String> getProductos() {
        return productos;
    }

    public double getTotal() {
        return total;
    }
}
