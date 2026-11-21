
package com.ferreteria.controllers;

import com.ferreteria.models.HistorialData;
import com.ferreteria.models.HistorialData.Compra;
import com.ferreteria.models.SesionData;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class HistorialClienteController {

    @FXML
    private TableView<Compra> tablaHistorial;

    @FXML
    private TableColumn<Compra, Integer> colNumero;

    @FXML
    private TableColumn<Compra, String> colFecha;

    @FXML
    private TableColumn<Compra, String> colProductos;

    @FXML
    private TableColumn<Compra, Double> colTotal;

    @FXML
    public void initialize() {

        if (!SesionData.esCliente()) {
            mostrarMensaje(
                    "Acceso denegado",
                    "Esta ventana es exclusiva para clientes."
            );
            return;
        }

        colNumero.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getNumero()
                )
        );

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFecha().format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                )
                        )
                )
        );

        colProductos.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.join(
                                ", ",
                                data.getValue().getProductos()
                        )
                )
        );

        colTotal.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getTotal()
                )
        );

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
                .append("\n\n");

        detalle.append("Fecha: ")
                .append(compra.getFecha().format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm"
                        )
                ))
                .append("\n\n");

        detalle.append("Productos comprados:\n");

        for (String producto : compra.getProductos()) {
            detalle.append("- ")
                    .append(producto)
                    .append("\n");
        }

        detalle.append("\nTotal pagado: $")
                .append(String.format(
                        "%,.2f",
                        compra.getTotal()
                ));

        mostrarMensaje("Detalle de compra", detalle.toString());
    }

    @FXML
    private void volverMenu() {

        if (!SesionData.esCliente()) {
            mostrarMensaje(
                    "Sesión no válida",
                    "Debes iniciar sesión como cliente."
            );
            return;
        }

        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fxml/MenuCliente.fxml")
            );

            Stage stage = (Stage) tablaHistorial
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Menú de clientes");
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarMensaje(
                    "Error",
                    "No se pudo abrir el menú de clientes."
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
