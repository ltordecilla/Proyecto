
package com.ferreteria.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class CarritoData {

    public static class Item {

        private Producto producto;
        private int cantidad;

        public Item(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getSubtotal() {
            return producto.getPrecio() * cantidad;
        }
    }

    // Carritos independientes por usuario
    private static final Map<String, ObservableList<Item>> carritos =
            new HashMap<>();

    // Obtener el carrito del cliente actual
    public static ObservableList<Item> getItems() {

        String usuario = SesionData.getUsuarioActual();

        if (!SesionData.esCliente() || usuario == null) {
            return FXCollections.observableArrayList();
        }

        return carritos.computeIfAbsent(
                usuario,
                u -> FXCollections.observableArrayList()
        );
    }

    // Agregar un producto
    public static void agregar(Producto producto) {

        if (!SesionData.esCliente() || producto == null) {
            return;
        }

        if (producto.getStock() <= 0) {
            return;
        }

        ObservableList<Item> items = getItems();

        for (Item item : items) {

            if (item.getProducto().getCodigo()
                    .equals(producto.getCodigo())) {

                if (item.getCantidad() < producto.getStock()) {
                    item.setCantidad(item.getCantidad() + 1);
                }

                return;
            }
        }

        items.add(new Item(producto, 1));
    }

    // Calcular el total del carrito actual
    public static double calcularTotal() {

        double total = 0;

        for (Item item : getItems()) {
            total += item.getSubtotal();
        }

        return total;
    }

    // Vaciar el carrito del cliente actual
    public static void vaciar() {
        if (SesionData.esCliente()) {
            getItems().clear();
        }
    }
}
