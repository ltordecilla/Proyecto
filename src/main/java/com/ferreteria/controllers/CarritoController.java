
package com.ferreteria.controllers;

import com.ferreteria.models.CarritoData;
import com.ferreteria.models.CarritoData.Item;
import com.ferreteria.models.Producto;
import com.ferreteria.models.SesionData;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class CarritoController {

    @FXML
    private VBox panelCarrito;

    @FXML
    private Label lblTotal;

    @FXML
    private void initialize() {

        Platform.runLater(() -> {

            if (!verificarSesion()) {
                abrirVentana("/fxml/Login.fxml", "Iniciar sesión");
                return;
            }

            actualizarCarrito();
        });
    }

    private boolean verificarSesion() {
        return SesionData.esAdministrador() || SesionData.esCliente();
    }

    private void actualizarCarrito() {

        panelCarrito.getChildren().clear();

        for (Item item : CarritoData.getItems()) {

            Producto producto = item.getProducto();

            HBox fila = new HBox(15);

            fila.setStyle(
                "-fx-padding: 12;" +
                "-fx-border-color: #cccccc;" +
                "-fx-background-color: white;"
            );

            ImageView imagen = new ImageView();

            imagen.setFitWidth(80);
            imagen.setFitHeight(80);
            imagen.setPreserveRatio(true);

            String ruta = producto.getImagen();

            if (ruta != null && !ruta.isEmpty()) {

                File archivo = new File(ruta);

                if (archivo.exists()) {
                    imagen.setImage(
                        new Image(archivo.toURI().toString())
                    );
                }
            }

            VBox informacion = new VBox(7);

            Label nombre = new Label(producto.getNombre());
            Label precio = new Label("Precio: $" + producto.getPrecio());
            Label cantidad = new Label("Cantidad: " + item.getCantidad());
            Label subtotal = new Label("Subtotal: $" + item.getSubtotal());

            informacion.getChildren().addAll(
                nombre, precio, cantidad, subtotal
            );

            Button aumentar = new Button("+");
            Button disminuir = new Button("-");
            Button eliminar = new Button("Eliminar");

            aumentar.setOnAction(e -> cambiarCantidad(item, 1));
            disminuir.setOnAction(e -> cambiarCantidad(item, -1));
            eliminar.setOnAction(e -> eliminarProducto(item));

            boolean esCliente = SesionData.esCliente();

            aumentar.setDisable(!esCliente);
            disminuir.setDisable(!esCliente);
            eliminar.setDisable(!esCliente);

            HBox botones = new HBox(8);

            botones.getChildren().addAll(
                aumentar, disminuir, eliminar
            );

            fila.getChildren().addAll(
                imagen, informacion, botones
            );

            panelCarrito.getChildren().add(fila);
        }

        if (CarritoData.getItems().isEmpty()) {
            panelCarrito.getChildren().add(
                new Label("Tu carrito está vacío.")
            );
        }

        lblTotal.setText(
            "TOTAL: $" + CarritoData.calcularTotal()
        );
    }

    private void cambiarCantidad(Item item, int cambio) {

        if (!SesionData.esCliente()) {
            return;
        }

        int nuevaCantidad = item.getCantidad() + cambio;

        if (nuevaCantidad <= 0) {
            eliminarProducto(item);
            return;
        }

        if (nuevaCantidad > item.getProducto().getStock()) {

            mostrarMensaje(
                "Stock insuficiente",
                "No hay suficientes unidades disponibles."
            );

            return;
        }

        item.setCantidad(nuevaCantidad);

        actualizarCarrito();
    }

    private void eliminarProducto(Item item) {

        if (!SesionData.esCliente()) {
            return;
        }

        CarritoData.getItems().remove(item);

        actualizarCarrito();
    }

    @FXML
    private void vaciarCarrito() {

        if (!SesionData.esCliente()) {

            mostrarMensaje(
                "Acceso restringido",
                "Solo los clientes pueden modificar el carrito."
            );

            return;
        }

        CarritoData.vaciar();

        actualizarCarrito();
    }

    @FXML
    private void finalizarCompra() {

        if (!SesionData.esCliente()) {

            mostrarMensaje(
                "Acceso restringido",
                "Solo los clientes pueden realizar compras."
            );

            return;
        }

        if (CarritoData.getItems().isEmpty()) {

            mostrarMensaje(
                "Carrito vacío",
                "Agrega productos antes de continuar."
            );

            return;
        }

        abrirVentana(
            "/fxml/Compra.fxml",
            "Finalizar compra"
        );
    }

    @FXML
    private void volverCatalogo() {

        if (!verificarSesion()) {
            abrirVentana("/fxml/Login.fxml", "Iniciar sesión");
            return;
        }

        abrirVentana(
            "/fxml/Catalogo.fxml",
            "Catálogo de productos"
        );
    }

    private void abrirVentana(String ruta, String titulo) {

        try {

            Parent root = FXMLLoader.load(
                getClass().getResource(ruta)
            );

            Stage stage = (Stage) panelCarrito.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);

        } catch (IOException | NullPointerException e) {

            mostrarMensaje(
                "Error",
                "No se pudo abrir la ventana solicitada."
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
