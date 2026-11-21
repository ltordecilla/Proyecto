

package com.ferreteria.controllers;

import com.ferreteria.Main;
import com.ferreteria.models.CarritoData;
import com.ferreteria.models.DeseosData;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeseosController {

    @FXML
    private FlowPane panelDeseos;

    // INICIALIZAR LISTA DE DESEOS
    @FXML
    public void initialize() {

        Platform.runLater(() -> {

            if (!SesionData.esCliente()) {

                mostrarMensaje(
                        "Acceso denegado",
                        "La lista de deseos es exclusiva para clientes."
                );

                return;
            }

            cargarDeseos();
        });
    }

    // CARGAR PRODUCTOS DE LA LISTA
    private void cargarDeseos() {

        panelDeseos.getChildren().clear();

        for (Producto producto : DeseosData.getDeseos()) {

            VBox tarjeta = new VBox(10);

            tarjeta.setPrefWidth(200);

            tarjeta.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-padding: 15;"
                    + "-fx-background-radius: 10;"
                    + "-fx-border-color: #dddddd;"
                    + "-fx-border-radius: 10;"
            );

            // IMAGEN
            ImageView imagen = new ImageView();

            imagen.setFitWidth(150);
            imagen.setFitHeight(110);
            imagen.setPreserveRatio(true);

            if (producto.getImagen() != null
                    && !producto.getImagen().isBlank()) {

                try {

                    imagen.setImage(new Image(
                            new java.io.File(producto.getImagen())
                                    .toURI().toString()
                    ));

                } catch (Exception e) {

                    System.out.println(
                            "No se pudo cargar la imagen."
                    );
                }
            }

            // INFORMACIÓN DEL PRODUCTO
            Label nombre = new Label(producto.getNombre());

            Label precio = new Label(
                    String.format("$%,.0f", producto.getPrecio())
            );

            Label stock = new Label(
                    "Stock: " + producto.getStock()
            );

            // BOTÓN AGREGAR AL CARRITO
            Button btnCarrito = new Button(
                    "AGREGAR AL CARRITO"
            );

            btnCarrito.setMaxWidth(Double.MAX_VALUE);

            btnCarrito.setOnAction(e -> {

                if (!SesionData.esCliente()) {
                    mostrarMensaje(
                            "Acceso denegado",
                            "Debes iniciar sesión como cliente."
                    );
                    return;
                }

                if (producto.getStock() > 0) {

                    CarritoData.agregar(producto);

                    mostrarMensaje(
                            "Producto agregado",
                            "Producto agregado al carrito."
                    );

                } else {

                    mostrarMensaje(
                            "Sin existencias",
                            "Este producto no tiene existencias."
                    );
                }
            });

            // BOTÓN ELIMINAR
            Button btnEliminar = new Button("ELIMINAR");

            btnEliminar.setMaxWidth(Double.MAX_VALUE);

            btnEliminar.setOnAction(e -> {

                if (!SesionData.esCliente()) {
                    mostrarMensaje(
                            "Acceso denegado",
                            "Debes iniciar sesión como cliente."
                    );
                    return;
                }

                DeseosData.eliminar(producto);

                cargarDeseos();
            });

            tarjeta.getChildren().addAll(
                    imagen,
                    nombre,
                    precio,
                    stock,
                    btnCarrito,
                    btnEliminar
            );

            panelDeseos.getChildren().add(tarjeta);
        }

        // LISTA VACÍA
        if (DeseosData.getDeseos().isEmpty()) {

            panelDeseos.getChildren().add(
                    new Label(
                            "No tienes productos en tu lista de deseos."
                    )
            );
        }
    }

    // VOLVER AL MENÚ DEL CLIENTE
    @FXML
    private void volverMenu() {

        if (SesionData.esCliente()) {

            abrirVentana(
                    "/fxml/MenuCliente.fxml",
                    "Menú de clientes"
            );

        } else {

            mostrarMensaje(
                    "Acceso denegado",
                    "Esta opción es exclusiva para clientes."
            );
        }
    }

    // ABRIR VENTANAS
    private void abrirVentana(String ruta, String titulo) {

        try {

            Parent root = FXMLLoader.load(
                    Main.class.getResource(ruta)
            );

            Stage stage = (Stage) panelDeseos
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {

            mostrarMensaje(
                    "Error",
                    "No se pudo abrir la ventana: "
                    + e.getMessage()
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
