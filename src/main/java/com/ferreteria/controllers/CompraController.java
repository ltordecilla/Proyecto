

package com.ferreteria.controllers;

import com.ferreteria.models.CompraData;
import com.ferreteria.models.CarritoData;
import com.ferreteria.models.SesionData;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CompraController {

    @FXML
    private Label lblTotal;

    @FXML
    private void initialize() {

        Platform.runLater(() -> {

            if (!SesionData.esCliente()) {
                mostrarMensaje(
                    "Acceso restringido",
                    "Solo los clientes pueden realizar compras."
                );

                abrirVentana(
                    "/fxml/Login.fxml",
                    "Iniciar sesión"
                );

                return;
            }

            lblTotal.setText(
                "TOTAL A PAGAR: $" + CarritoData.calcularTotal()
            );
        });
    }

    @FXML
    private void confirmarCompra() {

        if (!SesionData.esCliente()) {
            mostrarMensaje(
                "Acceso denegado",
                "No tienes permisos para realizar compras."
            );
            return;
        }

        if (CarritoData.getItems().isEmpty()) {
            mostrarMensaje(
                "Carrito vacío",
                "Agrega productos antes de comprar."
            );
            return;
        }

        boolean resultado = CompraData.finalizarCompra();

        if (resultado) {

            mostrarMensaje(
                "Compra exitosa",
                "Tu compra se realizó correctamente."
            );

            abrirVentana(
                "/fxml/Catalogo.fxml",
                "Catálogo de productos"
            );

        } else {

            mostrarMensaje(
                "Error en la compra",
                "No hay suficientes unidades disponibles. Verifica el stock."
            );
        }
    }

    @FXML
    private void cancelarCompra() {

        if (!SesionData.esCliente()) {
            abrirVentana(
                "/fxml/Login.fxml",
                "Iniciar sesión"
            );
            return;
        }

        abrirVentana(
            "/fxml/Carrito.fxml",
            "Mi carrito"
        );
    }

    private void abrirVentana(String ruta, String titulo) {

        try {

            Parent root = FXMLLoader.load(
                getClass().getResource(ruta)
            );

            Stage stage = (Stage) lblTotal.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);

        } catch (IOException | NullPointerException e) {

            mostrarMensaje(
                "Error",
                "No se pudo abrir la ventana."
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
