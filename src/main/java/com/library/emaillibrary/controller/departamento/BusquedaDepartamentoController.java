package com.library.emaillibrary.controller.departamento;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BusquedaDepartamentoController {

    @FXML private TextField txtNombre;

    // Interfaz funcional para pasar el filtro al padre
    public interface BusquedaDepartamentoListener {
        void onBuscar(String nombre);
    }

    private BusquedaDepartamentoListener listener;

    public void setBusquedaListener(BusquedaDepartamentoListener listener) {
        this.listener = listener;
    }

    @FXML
    void onActionBuscar(ActionEvent event) {
        if (listener != null) {
            String nombre = txtNombre.getText();
            // Enviamos el dato al listener
            listener.onBuscar(nombre);
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