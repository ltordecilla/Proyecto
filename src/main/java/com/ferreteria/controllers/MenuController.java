

package com.ferreteria.controllers;

import com.ferreteria.models.SesionData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private Label lblBienvenida;

    @FXML
    private void initialize() {

        // Verificar que el usuario sea administrador
        if (!SesionData.esAdministrador()) {
            mostrarMensaje(
                    "Acceso denegado",
                    "No tienes permisos para acceder al menú administrativo."
            );

            abrirVentana("/fxml/Login.fxml", "Iniciar sesión");
            return;
        }

        lblBienvenida.setText(
                "Bienvenido, Administrador: "
                + SesionData.getUsuarioActual()
        );
    }

    @FXML
    private void abrirProductos() {
        if (verificarAdministrador()) {
            abrirVentana(
                    "/fxml/Productos.fxml",
                    "Gestión de productos"
            );
        }
    }

    @FXML
    private void abrirCatalogo() {
        if (verificarAdministrador()) {
            abrirVentana(
                    "/fxml/Catalogo.fxml",
                    "Catálogo de productos"
            );
        }
    }

    @FXML
    private void abrirVentas() {
        if (verificarAdministrador()) {
            abrirVentana(
                    "/fxml/Ventas.fxml",
                    "Gestión de ventas"
            );
        }
    }

    @FXML
    private void abrirCarrito() {
        if (verificarAdministrador()) {
            abrirVentana(
                    "/fxml/Carrito.fxml",
                    "Mi carrito"
            );
        }
    }

    @FXML
    private void abrirHistorial() {
        if (verificarAdministrador()) {
            abrirVentana(
                    "/fxml/Historial.fxml",
                    "Historial de compras"
            );
        }
    }

    @FXML
    private void abrirDeseos() {
        if (verificarAdministrador()) {
            abrirVentana(
                    "/fxml/Deseos.fxml",
                    "Lista de deseos"
            );
        }
    }

    @FXML
    private void abrirReportes() {
        if (verificarAdministrador()) {
            abrirVentana(
                    "/fxml/Reportes.fxml",
                    "Reportes del sistema"
            );
        }
    }

    @FXML
    private void cerrarSesion() {

        SesionData.cerrarSesion();

        abrirVentana(
                "/fxml/Login.fxml",
                "Iniciar sesión"
        );
    }

    private boolean verificarAdministrador() {

        if (!SesionData.esAdministrador()) {

            mostrarMensaje(
                    "Acceso denegado",
                    "Esta función es exclusiva del administrador."
            );

            abrirVentana(
                    "/fxml/Login.fxml",
                    "Iniciar sesión"
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

            Stage stage = (Stage) lblBienvenida
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarMensaje(
                    "Error",
                    "No se pudo abrir: " + ruta
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
