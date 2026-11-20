
package com.ferreteria.controllers;

import com.ferreteria.models.SesionData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuClienteController {

    @FXML
    private void initialize() {

        if (!SesionData.esCliente()) {
            mostrarMensaje(
                "Acceso denegado",
                "No tienes permisos para acceder al menú de clientes."
            );

            abrirVentana("/fxml/Login.fxml", "Inicio de sesión");
        }
    }

    @FXML
    private void abrirCatalogo() {
        if (verificarCliente()) {
            abrirVentana(
                "/fxml/Catalogo.fxml",
                "Catálogo de productos"
            );
        }
    }

    @FXML
    private void abrirCarrito() {
        if (verificarCliente()) {
            abrirVentana(
                "/fxml/Carrito.fxml",
                "Mi carrito"
            );
        }
    }

    @FXML
    private void abrirDeseos() {
        if (verificarCliente()) {
            abrirVentana(
                "/fxml/Deseos.fxml",
                "Mi lista de deseos"
            );
        }
    }

    @FXML
    private void abrirHistorial() {
        if (verificarCliente()) {
            abrirVentana(
                "/fxml/Historial.fxml",
                "Mi historial de compras"
            );
        }
    }

    @FXML
    private void cerrarSesion() {

        // Cerrar la sesión actual
        SesionData.cerrarSesion();

        // Regresar al inicio de sesión
        abrirVentana(
            "/fxml/Login.fxml",
            "Inicio de sesión"
        );
    }

    private boolean verificarCliente() {

        if (!SesionData.esCliente()) {

            mostrarMensaje(
                "Sesión no válida",
                "Debes iniciar sesión como cliente."
            );

            SesionData.cerrarSesion();

            abrirVentana(
                "/fxml/Login.fxml",
                "Inicio de sesión"
            );

            return false;
        }

        return true;
    }

    private void abrirVentana(String ruta, String titulo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = obtenerStage();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarMensaje(
                "Error",
                "No se pudo abrir la ventana: " + ruta
            );

            e.printStackTrace();
        }
    }

    private Stage obtenerStage() {

        return javafx.stage.Window.getWindows()
                .stream()
                .filter(javafx.stage.Window::isShowing)
                .filter(ventana -> ventana instanceof Stage)
                .map(ventana -> (Stage) ventana)
                .findFirst()
                .orElseThrow();
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
