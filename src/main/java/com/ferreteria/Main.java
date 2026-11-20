
package com.ferreteria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Cargar Login.fxml desde resources/fxml
        URL ruta = getClass().getResource("/fxml/Login.fxml");

        if (ruta == null) {
            throw new RuntimeException(
                "No se encontró el archivo /fxml/Login.fxml"
            );
        }

        FXMLLoader loader = new FXMLLoader(ruta);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Ferretería - Sistema de Gestión");
        stage.setScene(scene);
        stage.setWidth(900);
        stage.setHeight(550);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
