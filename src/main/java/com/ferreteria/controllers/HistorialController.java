
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

import java.time.format.DateTimeFormatter;

public class HistorialController {

    @FXML
    private TableView<Compra> tablaHistorial;

    @FXML
    private TableColumn<Compra, Integer> colNumero;

    @FXML
    private TableColumn<Compra, String> colCliente;

    @FXML
    private TableColumn<Compra, String> colFecha;

    @FXML
    private TableColumn<Compra, String> colProductos;

    @FXML
    private TableColumn<Compra, Double> colTotal;

    // INICIALIZAR HISTORIAL
    @FXML
    public void initialize() {

        // Número de compra
        colNumero.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getNumero()
                )
        );

        // Nombre completo del cliente
        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombreCliente()
                )
        );

        // Fecha de compra
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFecha().format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                )
                        )
                )
        );

        // Productos comprados
        colProductos.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.join(
                                ", ",
                                data.getValue().getProductos()
                        )
                )
        );

        // Total pagado
        colTotal.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getTotal()
                )
        );

        // Cargar compras según el tipo de usuario
        tablaHistorial.getItems().setAll(
                HistorialData.getComprasVisibles()
        );
    }

    // VER DETALLE DE COMPRA
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

        detalle.append("Número de compra: ")
                .append(compra.getNumero())
                .append("\n\n");

        detalle.append("Cliente: ")
                .append(compra.getNombreCliente())
                .append("\n");

        detalle.append("Usuario: ")
                .append(compra.getUsuario())
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

        mostrarMensaje(
                "Detalle de compra",
                detalle.toString()
        );
    }

    // VOLVER AL MENÚ
    @FXML
    private void volverMenu() {

        String ruta;
        String titulo;

        if (SesionData.esAdministrador()) {

            ruta = "/fxml/Menu.fxml";
            titulo = "Panel del administrador";

        } else if (SesionData.esCliente()) {

            ruta = "/fxml/MenuCliente.fxml";
            titulo = "Panel del cliente";

        } else {

            mostrarMensaje(
                    "Sesión no válida",
                    "Debes iniciar sesión nuevamente."
            );

            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) tablaHistorial
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {

            mostrarMensaje(
                    "Error",
                    "No se pudo abrir el menú correspondiente."
            );

            e.printStackTrace();
        }
    }

    // MOSTRAR MENSAJES
    private void mostrarMensaje(
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
