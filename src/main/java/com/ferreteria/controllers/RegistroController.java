
package com.ferreteria.controllers;

import com.ferreteria.models.UsuarioData;
import com.ferreteria.models.UsuarioData.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistroController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtIdentificacion;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private PasswordField txtConfirmar;

    @FXML
    private void registrar() {

        String nombre = txtNombre.getText().trim();
        String identificacion = txtIdentificacion.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();
        String confirmar = txtConfirmar.getText();

        // Validar campos vacíos
        if (nombre.isEmpty() || identificacion.isEmpty()
                || correo.isEmpty() || telefono.isEmpty()
                || usuario.isEmpty() || contrasena.isEmpty()
                || confirmar.isEmpty()) {

            mostrarMensaje("Error", "Completa todos los campos.");
            return;
        }

        // Validar correo
        if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarMensaje("Error", "Ingresa un correo válido.");
            return;
        }

        // Validar identificación duplicada
        if (UsuarioData.existeIdentificacion(identificacion)) {
            mostrarMensaje("Error", "Esta identificación ya está registrada.");
            return;
        }

        // Validar correo duplicado
        if (UsuarioData.existeCorreo(correo)) {
            mostrarMensaje("Error", "Este correo ya está registrado.");
            return;
        }

        // Validar nombre de usuario duplicado
        if (UsuarioData.existeUsuario(usuario)) {
            mostrarMensaje("Error", "Este nombre de usuario ya existe.");
            return;
        }

        // Validar contraseña
        if (contrasena.length() < 6) {
            mostrarMensaje(
                "Error",
                "La contraseña debe tener mínimo 6 caracteres."
            );
            return;
        }

        if (!contrasena.equals(confirmar)) {
            mostrarMensaje("Error", "Las contraseñas no coinciden.");
            return;
        }

        // Crear usuario con rol CLIENTE
        Usuario nuevoUsuario = new Usuario(
            nombre,
            identificacion,
            correo,
            telefono,
            usuario,
            contrasena,
            "CLIENTE"
        );

        // Guardar usuario
        UsuarioData.registrarUsuario(nuevoUsuario);

        mostrarMensaje(
            "Registro exitoso",
            "El cliente " + nombre
            + " fue registrado correctamente.\n"
            + "Ya puedes iniciar sesión con tu usuario y contraseña."
        );

        limpiarCampos();
    }

    @FXML
    private void volver() {

        try {

            Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/Login.fxml")
            );

            Stage stage = (Stage) txtNombre.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Ferretería - Iniciar sesión");

        } catch (IOException e) {

            mostrarMensaje("Error", "No se pudo abrir el Login.");
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {

        txtNombre.clear();
        txtIdentificacion.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtUsuario.clear();
        txtContrasena.clear();
        txtConfirmar.clear();
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
