package com.library.emaillibrary.controller;

import com.library.emaillibrary.util.DataBaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.SQLException;

public class SarchEmailMenuController {
    @FXML
    private Label stateLabel;

    @FXML
    protected void onVerifyState() {
        stateLabel.setText("Verificando conexión...");
        try (Connection conn = DataBaseConnection.getConnection()) {

            if (conn != null) {
                // Si llegamos aquí, ¡la conexión funciona!
                stateLabel.setText("¡Conexión Exitosa!");

                // Un poco de estilo: Cambiamos el color del texto a VERDE
                stateLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            }

        } catch (SQLException e) {
            // Si algo falla (contraseña mal, BD apagada, etc.) entra aquí
            stateLabel.setText("Error al conectar");

            // Cambiamos el color a ROJO
            stateLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            e.printStackTrace(); // Muestra el error técnico en la consola del IDE
        }
    }

}



