package com.library.emaillibrary.controller.departamento;

import com.library.emaillibrary.controller.persona.PersonaRegistrarController;
import com.library.emaillibrary.model.DepartamentoModelo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BusquedaDepartamentoController {

    @FXML
    private TextField txtNombre;

    private DepartamentoWindowController parentController;

    public void setParentController(DepartamentoWindowController parentController) {
        this.parentController = parentController;
    }

    @FXML
    void onActionBuscar(ActionEvent event) {
        if (parentController != null) {
            // Extraemos el texto y le decimos al padre que filtre
            String nombreABuscar = txtNombre.getText();
            parentController.realizarBusqueda(nombreABuscar);
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
    private PersonaRegistrarController personaController;

    public void setPersonaRegistrarController(PersonaRegistrarController controller) {
        this.personaController = controller;
    }

    // En el botón "Seleccionar" o al doble click en la tabla:
    public void seleccionar() {
        DepartamentoModelo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (personaController != null && seleccionado != null) {
            personaController.recibirDepartamento(seleccionado);
            cerrarVentana();
        }
    }
}