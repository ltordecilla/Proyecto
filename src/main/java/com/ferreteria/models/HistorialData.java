
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

        public Compra(int numero, List<String> productos,
                      double total, String usuario) {

            this.numero = numero;
            this.fecha = LocalDateTime.now();
            this.productos = new ArrayList<>(productos);
            this.total = total;
            this.usuario = usuario;
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
    }

    public static List<Compra> compras = new ArrayList<>();

    public static void registrarCompra(
            List<String> productos, double total) {

        String usuario = SesionData.getUsuarioActual();

        if (usuario == null) {
            usuario = "Desconocido";
        }

        int numero = compras.size() + 1;

        Compra compra = new Compra(
                numero,
                productos,
                total,
                usuario
        );

        compras.add(compra);
    }

    // Consultar todas las compras
    public static List<Compra> getTodasLasCompras() {
        return new ArrayList<>(compras);
    }

    // Consultar compras de un cliente específico
    public static List<Compra> getComprasPorUsuario(String usuario) {

        List<Compra> resultado = new ArrayList<>();

        for (Compra compra : compras) {

            if (compra.getUsuario().equalsIgnoreCase(usuario)) {
                resultado.add(compra);
            }
        }

        return resultado;
    }

    // El administrador ve todas; el cliente solo las suyas
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
