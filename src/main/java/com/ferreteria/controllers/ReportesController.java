

package com.ferreteria.controllers;

import com.ferreteria.models.Producto;
import com.ferreteria.models.ProductoData;
import com.ferreteria.models.ReportesData;
import com.ferreteria.models.SesionData;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.PageSize;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

public class ReportesController {

    @FXML private Label lblTotalProductos;
    @FXML private Label lblDisponibles;
    @FXML private Label lblAgotados;
    @FXML private Label lblValorInventario;

    @FXML
    public void initialize() {

        Platform.runLater(() -> {

            if (!verificarAdministrador()) {
                volverSegunRol();
                return;
            }

            actualizarDatos();
        });
    }

    private boolean verificarAdministrador() {

        if (!SesionData.esAdministrador()) {

            mostrarMensaje(
                    "Acceso denegado",
                    "Solo el administrador puede consultar los reportes."
            );

            return false;
        }

        return true;
    }

    private void actualizarDatos() {

        lblTotalProductos.setText(
                String.valueOf(ReportesData.getTotalProductos()));

        lblDisponibles.setText(
                String.valueOf(ReportesData.getProductosDisponibles()));

        lblAgotados.setText(
                String.valueOf(ReportesData.getProductosAgotados()));

        lblValorInventario.setText(
                String.format(
                        "$%,.2f",
                        ReportesData.getValorInventario()
                ));
    }

    @FXML
    private void actualizarReporte() {

        if (!verificarAdministrador()) {
            return;
        }

        actualizarDatos();

        mostrarMensaje(
                "Reporte actualizado",
                "Las estadísticas fueron actualizadas correctamente."
        );
    }

    @FXML
    private void exportarPDF() {

        if (!verificarAdministrador()) {
            return;
        }

        FileChooser selector = new FileChooser();

        selector.setTitle("Guardar reporte PDF");
        selector.setInitialFileName("Reporte_Inventario.pdf");

        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Documento PDF", "*.pdf"
                ));

        File archivo = selector.showSaveDialog(
                lblTotalProductos.getScene().getWindow()
        );

        if (archivo == null) {
            return;
        }

        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(
                    archivo.getAbsolutePath() + ".pdf"
            );
        }

        Document documento = new Document(PageSize.A4.rotate());

        try {

            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream(archivo)
            );

            documento.open();

            Font titulo = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    20,
                    Color.BLUE
            );

            Font subtitulo = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    12,
                    Color.BLACK
            );

            // Título
            Paragraph encabezado = new Paragraph(
                    "FERRETERIA - REPORTE DE INVENTARIO",
                    titulo
            );

            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "RESUMEN GENERAL DEL INVENTARIO",
                    subtitulo
            ));

            documento.add(new Paragraph(
                    "Total de productos: "
                    + ReportesData.getTotalProductos()
            ));

            documento.add(new Paragraph(
                    "Productos disponibles: "
                    + ReportesData.getProductosDisponibles()
            ));

            documento.add(new Paragraph(
                    "Productos agotados: "
                    + ReportesData.getProductosAgotados()
            ));

            documento.add(new Paragraph(
                    "Valor del inventario: $"
                    + String.format(
                            "%,.2f",
                            ReportesData.getValorInventario()
                    )
            ));

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "DETALLE DE PRODUCTOS",
                    subtitulo
            ));

            // Tabla de productos
            PdfPTable tabla = new PdfPTable(6);

            tabla.setWidthPercentage(100);

            tabla.setWidths(new float[]{
                1.2f, 2.5f, 2.2f, 1.5f, 1.2f, 1.8f
            });

            String[] columnas = {
                "Código", "Nombre", "Categoría",
                "Precio", "Stock", "Valor total"
            };

            for (String columna : columnas) {

                PdfPCell celda = new PdfPCell(
                        new Phrase(columna)
                );

                celda.setBackgroundColor(
                        new Color(38, 50, 56)
                );

                celda.setHorizontalAlignment(
                        Element.ALIGN_CENTER
                );

                celda.setPadding(8);

                celda.setPhrase(new Phrase(
                        columna,
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                10,
                                Color.WHITE
                        )
                ));

                tabla.addCell(celda);
            }

            // Agregar productos
            for (Producto producto : ProductoData.productos) {

                tabla.addCell(producto.getCodigo());
                tabla.addCell(producto.getNombre());
                tabla.addCell(producto.getCategoria());

                tabla.addCell(String.format(
                        "$%,.2f",
                        producto.getPrecio()
                ));

                tabla.addCell(
                        String.valueOf(producto.getStock())
                );

                tabla.addCell(String.format(
                        "$%,.2f",
                        producto.getPrecio() * producto.getStock()
                ));
            }

            documento.add(tabla);

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "Reporte generado por el sistema de ferretería."
            ));

            documento.close();

            mostrarMensaje(
                    "Exportación exitosa",
                    "El reporte PDF se guardó correctamente en:\n"
                    + archivo.getAbsolutePath()
            );

        } catch (Exception e) {

            if (documento.isOpen()) {
                documento.close();
            }

            mostrarMensaje(
                    "Error al exportar",
                    e.getMessage()
            );

            e.printStackTrace();
        }
    }

    @FXML
    private void volverMenu() {
        volverSegunRol();
    }

    private void volverSegunRol() {

        String ruta;
        String titulo;

        if (SesionData.esAdministrador()) {

            ruta = "/fxml/Menu.fxml";
            titulo = "Panel del administrador";

        } else if (SesionData.esCliente()) {

            ruta = "/fxml/MenuCliente.fxml";
            titulo = "Panel del cliente";

        } else {

            ruta = "/fxml/Login.fxml";
            titulo = "Iniciar sesión";
        }

        abrirVentana(ruta, titulo);
    }

    private void abrirVentana(String ruta, String titulo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) lblTotalProductos
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {

            mostrarMensaje(
                    "Error",
                    "No se pudo abrir la ventana: " + ruta
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
