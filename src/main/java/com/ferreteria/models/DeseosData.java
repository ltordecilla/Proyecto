
package com.ferreteria.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class DeseosData {

    // Una lista de deseos independiente para cada cliente
    private static final Map<String, ObservableList<Producto>> listasDeseos =
            new HashMap<>();

    // Obtener la lista del cliente que inició sesión
    public static ObservableList<Producto> getDeseos() {

        String usuario = SesionData.getUsuarioActual();

        if (!SesionData.esCliente() || usuario == null) {
            return FXCollections.observableArrayList();
        }

        return listasDeseos.computeIfAbsent(
                usuario,
                u -> FXCollections.observableArrayList()
        );
    }

    // Agregar un producto a los deseos
    public static void agregar(Producto producto) {

        if (!SesionData.esCliente() || producto == null) {
            return;
        }

        ObservableList<Producto> deseos = getDeseos();

        for (Producto favorito : deseos) {

            if (favorito.getCodigo().equals(producto.getCodigo())) {
                return;
            }
        }

        deseos.add(producto);
    }

    // Eliminar un producto de los deseos
    public static void eliminar(Producto producto) {

        if (!SesionData.esCliente() || producto == null) {
            return;
        }

        getDeseos().removeIf(favorito ->
                favorito.getCodigo().equals(producto.getCodigo())
        );
    }

    // Comprobar si un producto está en los deseos
    public static boolean estaEnDeseos(Producto producto) {

        if (!SesionData.esCliente() || producto == null) {
            return false;
        }

        return getDeseos().stream().anyMatch(favorito ->
                favorito.getCodigo().equals(producto.getCodigo())
        );
    }

    // Vaciar los deseos del cliente actual
    public static void vaciar() {

        if (!SesionData.esCliente()) {
            return;
        }

        getDeseos().clear();
    }
}
