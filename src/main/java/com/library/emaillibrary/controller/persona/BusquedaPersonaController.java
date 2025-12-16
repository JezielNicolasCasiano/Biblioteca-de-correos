package com.library.emaillibrary.controller.persona;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;

public class BusquedaPersonaController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPaterno;
    @FXML private TextField txtMaterno;
    @FXML private DatePicker dpFechaNac;     // Fecha Inicio (Desde)
    @FXML private DatePicker dpFechaNac1;    // Fecha Fin (Hasta)

    // Interfaz para pasar los filtros al padre
    public interface BusquedaPersonaListener {
        void onBuscar(String nombre, String paterno, String materno, LocalDate fechaInicio, LocalDate fechaFin);
    }

    private BusquedaPersonaListener listener;

    public void setBusquedaListener(BusquedaPersonaListener listener) {
        this.listener = listener;
    }

    @FXML
    void onActionBuscar(ActionEvent event) {
        if (listener != null) {
            listener.onBuscar(
                    txtNombre.getText(),
                    txtPaterno.getText(),
                    txtMaterno.getText(),
                    dpFechaNac.getValue(),
                    dpFechaNac1.getValue()
            );
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