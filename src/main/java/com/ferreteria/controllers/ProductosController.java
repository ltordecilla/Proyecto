
package com.ferreteria.controllers;

import com.ferreteria.models.Producto;
import com.ferreteria.models.ProductoData;
import com.ferreteria.models.SesionData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ProductosController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;

    @FXML private ComboBox<String> cmbCategoria;

    @FXML private ImageView imgProducto;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colImagen;

    private final ObservableList<Producto> productos =
            ProductoData.productos;

    private String rutaImagen = "";

    @FXML
    private void initialize() {

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Herramientas",
                "Construcción",
                "Pinturas",
                "Electricidad",
                "Plomería",
                "Seguridad",
                "Ferretería general"
        ));

        colCodigo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCodigo()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNombre()));

        colCategoria.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategoria()));

        colPrecio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getPrecio()));

        colStock.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getStock()));

        colImagen.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getImagen()));

        colImagen.setCellFactory(column -> new TableCell<>() {

            private final ImageView imagen = new ImageView();

            @Override
            protected void updateItem(String ruta, boolean empty) {
                super.updateItem(ruta, empty);

                if (empty || ruta == null || ruta.isEmpty()) {
                    setGraphic(null);
                    return;
                }

                File archivo = new File(ruta);

                if (archivo.exists()) {
                    imagen.setImage(
                            new Image(archivo.toURI().toString())
                    );

                    imagen.setFitWidth(60);
                    imagen.setFitHeight(50);
                    imagen.setPreserveRatio(true);

                    setGraphic(imagen);
                } else {
                    setGraphic(null);
                }
            }
        });

        tablaProductos.setItems(productos);

        tablaProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarCampos(seleccionado);
                    }
                });

        // Verificar el acceso cuando la ventana ya esté cargada
        Platform.runLater(() -> {
            if (!SesionData.esAdministrador()) {
                mostrarMensaje(
                        "Acceso denegado",
                        "Solo el administrador puede gestionar productos."
                );

                volverSegunRol();
            }
        });
    }

    private boolean verificarAdministrador() {

        if (!SesionData.esAdministrador()) {

            mostrarMensaje(
                    "Acceso denegado",
                    "Esta función es exclusiva del administrador."
            );

            volverSegunRol();
            return false;
        }

        return true;
    }

    @FXML
    private void seleccionarImagen() {

        if (!verificarAdministrador()) {
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar imagen del producto");

        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes", "*.png", "*.jpg", "*.jpeg"
                )
        );

        File archivo = selector.showOpenDialog(
                imgProducto.getScene().getWindow()
        );

        if (archivo != null) {
            rutaImagen = archivo.getAbsolutePath();

            imgProducto.setImage(
                    new Image(archivo.toURI().toString())
            );
        }
    }

    @FXML
    private void agregarProducto() {

        if (!verificarAdministrador()) {
            return;
        }

        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String categoria = cmbCategoria.getValue();

            if (codigo.isEmpty() || nombre.isEmpty()
                    || categoria == null
                    || txtPrecio.getText().trim().isEmpty()
                    || txtStock.getText().trim().isEmpty()) {

                mostrarMensaje("Error", "Completa todos los campos.");
                return;
            }

            if (rutaImagen.isEmpty()) {
                mostrarMensaje("Error", "Selecciona una imagen.");
                return;
            }

            for (Producto p : productos) {
                if (p.getCodigo().equalsIgnoreCase(codigo)) {
                    mostrarMensaje("Error", "El código ya existe.");
                    return;
                }
            }

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim());

            int stock = Integer.parseInt(
                    txtStock.getText().trim());

            if (precio <= 0 || stock < 0) {
                mostrarMensaje("Error", "Precio o stock inválido.");
                return;
            }

            Producto producto = new Producto(
                    codigo, nombre, categoria,
                    precio, stock, rutaImagen
            );

            productos.add(producto);

            mostrarMensaje(
                    "Éxito",
                    "Producto registrado correctamente."
            );

            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Error",
                    "El precio y el stock deben ser numéricos."
            );
        }
    }

    @FXML
    private void modificarProducto() {

        if (!verificarAdministrador()) {
            return;
        }

        Producto seleccionado =
                tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje("Error", "Selecciona un producto.");
            return;
        }

        try {
            String nombre = txtNombre.getText().trim();
            String categoria = cmbCategoria.getValue();

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim());

            int stock = Integer.parseInt(
                    txtStock.getText().trim());

            if (nombre.isEmpty() || categoria == null
                    || precio <= 0 || stock < 0) {

                mostrarMensaje("Error", "Verifica los datos.");
                return;
            }

            seleccionado.setNombre(nombre);
            seleccionado.setCategoria(categoria);
            seleccionado.setPrecio(precio);
            seleccionado.setStock(stock);

            if (!rutaImagen.isEmpty()) {
                seleccionado.setImagen(rutaImagen);
            }

            tablaProductos.refresh();

            mostrarMensaje(
                    "Éxito",
                    "Producto modificado correctamente."
            );

            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Error",
                    "Precio y stock deben ser numéricos."
            );
        }
    }

    @FXML
    private void eliminarProducto() {

        if (!verificarAdministrador()) {
            return;
        }

        Producto seleccionado =
                tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje("Error", "Selecciona un producto.");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar producto");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Deseas eliminar este producto?");

        if (alerta.showAndWait().orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            productos.remove(seleccionado);
            limpiarCampos();
        }
    }

    private void cargarCampos(Producto producto) {

        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        cmbCategoria.setValue(producto.getCategoria());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));

        rutaImagen = producto.getImagen();

        if (rutaImagen != null && !rutaImagen.isEmpty()
                && new File(rutaImagen).exists()) {

            imgProducto.setImage(
                    new Image(new File(rutaImagen).toURI().toString())
            );

        } else {
            imgProducto.setImage(null);
        }
    }

    @FXML
    private void limpiarCampos() {

        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();

        rutaImagen = "";
        imgProducto.setImage(null);

        tablaProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void volverMenu() {
        volverSegunRol();
    }

    private void volverSegunRol() {

        if (SesionData.esAdministrador()) {
            abrirVentana("/fxml/Menu.fxml", "Panel del administrador");

        } else if (SesionData.esCliente()) {
            abrirVentana("/fxml/MenuCliente.fxml", "Panel del cliente");

        } else {
            abrirVentana("/fxml/Login.fxml", "Iniciar sesión");
        }
    }

    private void abrirVentana(String ruta, String titulo) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) tablaProductos
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
