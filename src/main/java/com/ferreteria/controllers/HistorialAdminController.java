
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

public class HistorialAdminController {

    @FXML
    private TableView<Compra> tablaHistorial;

    @FXML
    private TableColumn<Compra, Integer> colNumero;

    @FXML
    private TableColumn<Compra, String> colCliente;

    @FXML
    private TableColumn<Compra, String> colUsuario;

    @FXML
    private TableColumn<Compra, String> colFecha;

    @FXML
    private TableColumn<Compra, String> colProductos;

    @FXML
    private TableColumn<Compra, Double> colTotal;

    @FXML
    public void initialize() {

        if (!SesionData.esAdministrador()) {
            mostrarMensaje(
                    "Acceso denegado",
                    "Esta ventana es exclusiva del administrador."
            );
            return;
        }

        colNumero.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getNumero()
                )
        );

        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombreCliente()
                )
        );

        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getUsuario()
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
                HistorialData.getTodasLasCompras()
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
                    "Selecciona una compra."
            );
            return;
        }

        StringBuilder detalle = new StringBuilder();

        detalle.append("CLIENTE: ")
                .append(compra.getNombreCliente())
                .append("\n");

        detalle.append("USUARIO: ")
                .append(compra.getUsuario())
                .append("\n");

        detalle.append("COMPRA N.º: ")
                .append(compra.getNumero())
                .append("\n");

        detalle.append("FECHA: ")
                .append(compra.getFecha().format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm"
                        )
                ))
                .append("\n\nPRODUCTOS:\n");

        for (String producto : compra.getProductos()) {
            detalle.append("- ")
                    .append(producto)
                    .append("\n");
        }

        detalle.append("\nTOTAL PAGADO: $")
                .append(String.format(
                        "%,.2f",
                        compra.getTotal()
                ));

        mostrarMensaje("Detalle de compra", detalle.toString());
    }

    @FXML
    private void volverMenu() {
        abrirVentana(
                "/fxml/Menu.fxml",
                "Panel administrativo"
        );
    }

    private void abrirVentana(String ruta, String titulo) {

        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource(ruta)
            );

            Stage stage = (Stage) tablaHistorial
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarMensaje(
                    "Error",
                    "No se pudo abrir el menú: " + e.getMessage()
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
