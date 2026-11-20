
package com.ferreteria.controllers;

import com.ferreteria.models.SesionData;
import com.ferreteria.models.UsuarioData;
import com.ferreteria.models.UsuarioData.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
 
    @FXML
    private TextField txtUsuario;
@FXML
private void salir() {
    javafx.application.Platform.exit();
}
    @FXML
    private PasswordField txtContrasena;

    @FXML
    private void initialize() {
        SesionData.cerrarSesion();
    }

    @FXML
    private void iniciarSesion() {

        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarMensaje("Campos vacíos",
                    "Ingresa tu usuario y contraseña.");
            return;
        }

        // Acceso del administrador
        if (usuario.equalsIgnoreCase("admin")
                && contrasena.equals("admin123")) {

            SesionData.iniciarSesion("admin", "ADMIN");

            abrirVentana("/fxml/Menu.fxml",
                    "Panel administrativo");
            return;
        }

        // Validar clientes registrados
        Usuario cliente = UsuarioData.validarLogin(
                usuario, contrasena
        );

        if (cliente != null && "CLIENTE".equals(cliente.getRol())) {

            SesionData.iniciarSesion(
                    cliente.getUsuario(),
                    cliente.getRol()
            );

            abrirVentana("/fxml/MenuCliente.fxml",
                    "Menú de clientes");

        } else {
            mostrarMensaje("Acceso denegado",
                    "Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void abrirRegistro() {
        abrirVentana("/fxml/Registro.fxml",
                "Registro de clientes");
    }

    private void abrirVentana(String ruta, String titulo) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (IOException | NullPointerException e) {
            mostrarMensaje("Error",
                    "No se pudo abrir la ventana solicitada.");
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
