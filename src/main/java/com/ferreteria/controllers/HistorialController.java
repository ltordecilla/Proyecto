
package com.ferreteria.controllers;

import com.ferreteria.models.HistorialData;
import com.ferreteria.models.HistorialData.Compra;
import com.ferreteria.models.SesionData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HistorialController {

    @FXML private TableView<Compra> tablaHistorial;
    @FXML private TableColumn<Compra, Integer> colNumero;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colProductos;
    @FXML private TableColumn<Compra, Double> colTotal;

    @FXML
    public void initialize() {

        colNumero.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getNumero()
                ));

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFecha().format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                )
                        )
                ));

        colProductos.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.join(
                                ", ",
                                data.getValue().getProductos()
                        )
                ));

        colTotal.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getTotal()
                ));

        // Mostrar solamente las compras permitidas para el usuario
        tablaHistorial.getItems().setAll(
                HistorialData.getComprasVisibles()
        );
    }

    @FXML
    private void verDetalle() {

        Compra compra = tablaHistorial
                .getSelectionModel()
                .getSelectedItem();

        if (compra == null) {
            mostrarMensaje(
                    "Atención",
                    "Selecciona una compra para ver sus detalles."
            );
            return;
        }

        StringBuilder detalle = new StringBuilder();

        detalle.append("Compra número: ")
                .append(compra.getNumero())
                .append("\n");

        detalle.append("Cliente: ")
                .append(compra.getUsuario())
                .append("\n\n");

        detalle.append("Productos:\n");

        for (String producto : compra.getProductos()) {
            detalle.append("- ")
                    .append(producto)
                    .append("\n");
        }

        detalle.append("\nTotal: $")
                .append(String.format("%.2f", compra.getTotal()));

        mostrarMensaje("Detalle de compra", detalle.toString());
    }

    @FXML
    private void volverMenu() {

        String ruta;

        if (SesionData.esAdministrador()) {
            ruta = "/fxml/Menu.fxml";
        } else {
            ruta = "/fxml/MenuCliente.fxml";
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) tablaHistorial
                    .getScene().getWindow();

            stage.setScene(new Scene(root));

            stage.setTitle(
                    SesionData.esAdministrador()
                    ? "Panel del administrador"
                    : "Panel del cliente"
            );

            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarMensaje(
                    "Error",
                    "No se pudo abrir el menú correspondiente."
            );

            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
