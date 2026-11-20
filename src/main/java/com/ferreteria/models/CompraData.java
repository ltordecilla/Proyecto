
package com.ferreteria.models;

import java.util.ArrayList;
import java.util.List;

public class CompraData {

    public static boolean finalizarCompra() {

        // Verificar que el usuario sea cliente
        if (!SesionData.esCliente()) {
            return false;
        }

        // Obtener el carrito del cliente actual
        List<CarritoData.Item> items = CarritoData.getItems();

        // Verificar que el carrito no esté vacío
        if (items.isEmpty()) {
            return false;
        }

        // Validar todos los productos antes de comprar
        for (CarritoData.Item item : items) {

            Producto producto = item.getProducto();

            if (producto == null
                    || item.getCantidad() <= 0
                    || producto.getStock() < item.getCantidad()) {

                return false;
            }
        }

        // Calcular el total
        double total = CarritoData.calcularTotal();

        // Crear descripción de los productos
        List<String> descripciones = new ArrayList<>();

        for (CarritoData.Item item : items) {

            Producto producto = item.getProducto();

            String descripcion =
                    producto.getNombre()
                    + " - Cantidad: " + item.getCantidad()
                    + " - Precio: $" + producto.getPrecio()
                    + " - Subtotal: $" + item.getSubtotal();

            descripciones.add(descripcion);
        }

        // Registrar compra en el historial
        HistorialData.registrarCompra(descripciones, total);

        // Registrar venta para el administrador
        VentaData.registrarVenta(descripciones, total);

        // Descontar productos del inventario
        for (CarritoData.Item item : items) {

            Producto producto = item.getProducto();

            producto.setStock(
                    producto.getStock() - item.getCantidad()
            );
        }

        // Vaciar únicamente el carrito del cliente actual
        CarritoData.vaciar();

        return true;
    }
}
