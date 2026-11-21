
package com.ferreteria.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistorialData {

    public static class Compra {

        private int numero;
        private LocalDateTime fecha;
        private List<String> productos;
        private double total;
        private String usuario;
        private String nombreCliente;

        public Compra(int numero, List<String> productos,
                      double total, String usuario,
                      String nombreCliente) {

            this.numero = numero;
            this.fecha = LocalDateTime.now();
            this.productos = new ArrayList<>(productos);
            this.total = total;
            this.usuario = usuario;
            this.nombreCliente = nombreCliente;
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

        public String getUsuario() {
            return usuario;
        }

        public String getNombreCliente() {
            return nombreCliente;
        }
    }

    public static List<Compra> compras = new ArrayList<>();

    // REGISTRAR COMPRA
    public static void registrarCompra(
            List<String> productos, double total) {

        String usuario = SesionData.getUsuarioActual();

        if (usuario == null) {
            usuario = "Desconocido";
        }

        String nombreCliente = usuario;

        // Buscar nombre completo del cliente registrado
        for (UsuarioData.Usuario cliente : UsuarioData.usuarios) {

            if (cliente.getUsuario().equalsIgnoreCase(usuario)) {
                nombreCliente = cliente.getNombre();
                break;
            }
        }

        int numero = compras.size() + 1;

        Compra compra = new Compra(
                numero,
                productos,
                total,
                usuario,
                nombreCliente
        );

        compras.add(compra);
    }

    // CONSULTAR TODAS LAS COMPRAS
    public static List<Compra> getTodasLasCompras() {
        return new ArrayList<>(compras);
    }

    // CONSULTAR COMPRAS DE UN CLIENTE
    public static List<Compra> getComprasPorUsuario(String usuario) {

        List<Compra> resultado = new ArrayList<>();

        for (Compra compra : compras) {

            if (compra.getUsuario().equalsIgnoreCase(usuario)) {
                resultado.add(compra);
            }
        }

        return resultado;
    }

    // COMPRAS VISIBLES SEGÚN EL ROL
    public static List<Compra> getComprasVisibles() {

        if (SesionData.esAdministrador()) {
            return getTodasLasCompras();
        }

        String usuario = SesionData.getUsuarioActual();

        if (usuario == null) {
            return new ArrayList<>();
        }

        return getComprasPorUsuario(usuario);
    }
}
