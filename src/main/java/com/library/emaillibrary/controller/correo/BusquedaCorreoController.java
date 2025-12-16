package com.library.emaillibrary.controller.correo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BusquedaCorreoController {

    @FXML private TextField txtParteLocal;
    @FXML private TextField txtDominio;

    // Interfaz para comunicarse con la ventana padre
    public interface BusquedaCorreoListener {
        void onBuscar(String parteLocal, String dominio);
    }

    private BusquedaCorreoListener listener;

    public void setBusquedaListener(BusquedaCorreoListener listener) {
        this.listener = listener;
    }

    @FXML
    void onActionBuscar(ActionEvent event) {
        if (listener != null) {
            String parteLocal = txtParteLocal.getText();
            String dominio = txtDominio.getText();

            // Enviamos los filtros al padre
            listener.onBuscar(parteLocal, dominio);
        }
        cerrarVentana(event);
    }

    @FXML
    void onActionCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}