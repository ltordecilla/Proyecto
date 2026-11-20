

package com.ferreteria.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class VentaData {

    public static ObservableList<Venta> ventas =
            FXCollections.observableArrayList();

    private static int siguienteNumero = 1;

    public static void registrarVenta(List<String> productos, double total) {
        Venta venta = new Venta(siguienteNumero, productos, total);

        ventas.add(venta);
        siguienteNumero++;
    }

    public static int getTotalVentas() {
        return ventas.size();
    }

    public static double getTotalIngresos() {
        return ventas.stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }
}
