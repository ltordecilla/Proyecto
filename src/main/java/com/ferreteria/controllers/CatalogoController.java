
package com.ferreteria.controllers;

import com.ferreteria.models.Producto;
import com.ferreteria.models.ProductoData;
import com.ferreteria.models.CarritoData;
import com.ferreteria.models.DeseosData;
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
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class CatalogoController {

    @FXML
    private TilePane panelProductos;

    @FXML
    private void initialize() {

        Platform.runLater(() -> {

            if (!verificarSesion()) {
                abrirVentana(
                    "/fxml/Login.fxml",
                    "Iniciar sesión"
                );
                return;
            }

            cargarProductos();
        });
    }

    private void cargarProductos() {

        panelProductos.getChildren().clear();

        for (Producto producto : ProductoData.productos) {

            VBox tarjeta = new VBox(8);

            tarjeta.setPrefWidth(190);
            tarjeta.setPrefHeight(320);

            tarjeta.setStyle(
                "-fx-padding: 12;" +
                "-fx-background-color: white;" +
                "-fx-border-color: #cccccc;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
            );

            // Imagen del producto
            ImageView imagen = new ImageView();

            imagen.setFitWidth(160);
            imagen.setFitHeight(110);
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

            // Nombre
            Label nombre = new Label(
                producto.getNombre()
            );

            nombre.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: bold;"
            );

            // Categoría
            Label categoria = new Label(
                producto.getCategoria()
            );

            // Precio
            Label precio = new Label(
                "Precio: $" + producto.getPrecio()
            );

            // Disponibilidad
            Label stock = new Label(
                producto.getStock() > 0
                ? "Disponible: " + producto.getStock()
                : "AGOTADO"
            );

            // Botón carrito
            Button btnAgregar = new Button(
                "AGREGAR AL CARRITO"
            );

            btnAgregar.setMaxWidth(Double.MAX_VALUE);

            btnAgregar.setDisable(
                !SesionData.esCliente()
                || producto.getStock() <= 0
            );

            btnAgregar.setOnAction(
                e -> agregarAlCarrito(producto)
            );

            // Botón deseos
            Button btnDeseos = new Button(
                "AGREGAR A DESEOS"
            );

            btnDeseos.setMaxWidth(Double.MAX_VALUE);

            if (SesionData.esCliente()
                    && DeseosData.estaEnDeseos(producto)) {

                btnDeseos.setText("EN MIS DESEOS");
            }

            btnDeseos.setDisable(!SesionData.esCliente());

            btnDeseos.setOnAction(e -> {

                if (!verificarCliente()) {
                    return;
                }

                if (DeseosData.estaEnDeseos(producto)) {

                    mostrarMensaje(
                        "Lista de deseos",
                        "Este producto ya está en tus favoritos."
                    );

                } else {

                    DeseosData.agregar(producto);

                    btnDeseos.setText("EN MIS DESEOS");

                    mostrarMensaje(
                        "Producto guardado",
                        producto.getNombre()
                        + " fue agregado a tu lista de deseos."
                    );
                }
            });

            tarjeta.getChildren().addAll(
                imagen,
                nombre,
                categoria,
                precio,
                stock,
                btnAgregar,
                btnDeseos
            );

            panelProductos.getChildren().add(tarjeta);
        }

        if (ProductoData.productos.isEmpty()) {

            panelProductos.getChildren().add(
                new Label("No hay productos registrados.")
            );
        }
    }

    // Agregar producto al carrito del cliente
    private void agregarAlCarrito(Producto producto) {

        if (!verificarCliente()) {
            return;
        }

        if (producto.getStock() <= 0) {

            mostrarMensaje(
                "Producto agotado",
                "Este producto no tiene existencias."
            );

            return;
        }

        int cantidadAnterior = 0;

        for (CarritoData.Item item : CarritoData.getItems()) {

            if (item.getProducto().getCodigo()
                    .equals(producto.getCodigo())) {

                cantidadAnterior = item.getCantidad();
                break;
            }
        }

        CarritoData.agregar(producto);

        int cantidadNueva = 0;

        for (CarritoData.Item item : CarritoData.getItems()) {

            if (item.getProducto().getCodigo()
                    .equals(producto.getCodigo())) {

                cantidadNueva = item.getCantidad();
                break;
            }
        }

        if (cantidadNueva > cantidadAnterior) {

            mostrarMensaje(
                "Producto agregado",
                producto.getNombre()
                + " fue agregado a tu carrito."
            );

        } else {

            mostrarMensaje(
                "Stock máximo",
                "No puedes agregar más unidades de este producto."
            );
        }
    }

    private boolean verificarCliente() {

        if (!SesionData.esCliente()) {

            mostrarMensaje(
                "Acceso denegado",
                "Debes iniciar sesión como cliente para realizar compras."
            );

            return false;
        }

        return true;
    }

    @FXML
    private void abrirCarrito() {

        if (!verificarCliente()) {
            return;
        }

        abrirVentana(
            "/fxml/Carrito.fxml",
            "Mi carrito"
        );
    }

    @FXML
    private void volverMenu() {

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

    private boolean verificarSesion() {

        return SesionData.esAdministrador()
                || SesionData.esCliente();
    }

    private void abrirVentana(String ruta, String titulo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) panelProductos
                .getScene()
                .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarMensaje(
                "Error",
                "No se pudo abrir la ventana: " + ruta
            );

            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alerta = new Alert(
            Alert.AlertType.INFORMATION
        );

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
