
package com.ferreteria.models;

public class ReportesData {

    // Cantidad total de productos
    public static int getTotalProductos() {
        return ProductoData.productos.size();
    }

    // Productos disponibles
    public static int getProductosDisponibles() {

        int disponibles = 0;

        for (Producto producto : ProductoData.productos) {
            if (producto.getStock() > 0) {
                disponibles++;
            }
        }

        return disponibles;
    }

    // Productos agotados
    public static int getProductosAgotados() {

        int agotados = 0;

        for (Producto producto : ProductoData.productos) {
            if (producto.getStock() <= 0) {
                agotados++;
            }
        }

        return agotados;
    }

    // Valor total del inventario
    public static double getValorInventario() {

        double total = 0;

        for (Producto producto : ProductoData.productos) {
            total += producto.getPrecio() * producto.getStock();
        }

        return total;
    }
}
