package com.library.emaillibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Asegúrate de que el nombre del FXML sea correcto (main-window-all.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("main-window-all.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sistema de Gestión de Biblioteca");
        stage.setScene(scene);
        stage.show();
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA ---
    public static void main(String[] args) {
        launch();
    }
}