

package com.ferreteria.controllers;

import com.ferreteria.models.Venta;
import com.ferreteria.models.VentaData;
import com.ferreteria.models.SesionData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class VentasController {

    @FXML
    private TableView<Venta> tablaVentas;

    @FXML
    private TableColumn<Venta, Integer> colNumero;

    @FXML
    private TableColumn<Venta, String> colFecha;

    @FXML
    private TableColumn<Venta, String> colProductos;

    @FXML
    private TableColumn<Venta, Double> colTotal;

    @FXML
    private Label lblTotalVentas;

    @FXML
    private Label lblIngresos;

    @FXML
    public void initialize() {

        colNumero.setCellValueFactory(
                new PropertyValueFactory<>("numero"));

        colFecha.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleStringProperty(
                        datos.getValue().getFecha().format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                ))));

        colProductos.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleStringProperty(
                        String.join(
                                ", ",
                                datos.getValue().getProductos()
                        )));

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("total"));

        // Verificar permisos después de cargar la interfaz
        Platform.runLater(() -> {

            if (!verificarAdministrador()) {
                volverSegunRol();
                return;
            }

            actualizarTabla();
        });
    }

    private boolean verificarAdministrador() {

        if (!SesionData.esAdministrador()) {

            mostrarAlerta(
                    "Acceso denegado. "
                    + "Solo el administrador puede consultar las ventas."
            );

            return false;
        }

        return true;
    }

    @FXML
    public void actualizarTabla() {

        if (!verificarAdministrador()) {
            return;
        }

        tablaVentas.setItems(
                FXCollections.observableArrayList(
                        VentaData.ventas
                )
        );

        lblTotalVentas.setText(
                String.valueOf(VentaData.getTotalVentas())
        );

        lblIngresos.setText(
                String.format(
                        "$ %.2f",
                        VentaData.getTotalIngresos()
                )
        );
    }

    @FXML
    public void verDetalle() {

        if (!verificarAdministrador()) {
            return;
        }

        Venta venta = tablaVentas
                .getSelectionModel()
                .getSelectedItem();

        if (venta == null) {
            mostrarAlerta(
                    "Seleccione una venta para ver sus detalles."
            );
            return;
        }

        String detalle = String.join(
                "\n",
                venta.getProductos()
        );

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Detalle de venta");
        alerta.setHeaderText(
                "Venta #" + venta.getNumero()
        );

        alerta.setContentText(
                "Productos:\n" + detalle
                + "\n\nTotal: $ "
                + String.format("%.2f", venta.getTotal())
        );

        alerta.showAndWait();
    }

    @FXML
    public void volverMenu() {
        volverSegunRol();
    }

    private void volverSegunRol() {

        if (SesionData.esAdministrador()) {

            abrirVentana(
                    "/fxml/Menu.fxml",
                    "Panel del administrador"
            );

        } else if (SesionData.esCliente()) {

            abrirVentana(
                    "/fxml/MenuCliente.fxml",
                    "Panel del cliente"
            );

        } else {

            abrirVentana(
                    "/fxml/Login.fxml",
                    "Iniciar sesión"
            );
        }
    }

    private void abrirVentana(String ruta, String titulo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) tablaVentas
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarAlerta(
                    "No se pudo abrir la ventana: " + ruta
            );

            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.WARNING);

        alerta.setTitle("Gestión de ventas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
