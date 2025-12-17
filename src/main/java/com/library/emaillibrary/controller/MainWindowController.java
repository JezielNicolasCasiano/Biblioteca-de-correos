package com.library.emaillibrary.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainWindowController {

    // Inyectamos el BorderPane del FXML para poder cambiar su centro
    @FXML
    private BorderPane mainContainer;

    @FXML
    void showPersona(ActionEvent event) {
        loadView("/com/library/emaillibrary/persona-window.fxml");
    }

    @FXML
    void showDepartamento(ActionEvent event) {
        loadView("/com/library/emaillibrary/departamento-window.fxml");
    }

    @FXML
    void showCorreo(ActionEvent event) {
        loadView("/com/library/emaillibrary/correos-window.fxml");
    }

    @FXML
    void closeApplication(ActionEvent event) {
        Platform.exit();
    }

    // Método auxiliar para cargar vistas y ponerlas al centro
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainContainer.setCenter(view); // AQUÍ OCURRE LA MAGIA
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Navegación", "No se pudo cargar la vista: " + fxmlPath + "\nVerifica que el archivo FXML exista.");
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }
}