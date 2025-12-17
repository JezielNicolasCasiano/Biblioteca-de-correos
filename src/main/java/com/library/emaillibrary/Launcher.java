package com.library.emaillibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("main-window-all.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("Sistema de Gestión de Biblioteca de Correos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}