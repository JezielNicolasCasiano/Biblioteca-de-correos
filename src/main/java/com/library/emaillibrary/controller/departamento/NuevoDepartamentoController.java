package com.library.emaillibrary.controller.departamento;

import com.library.emaillibrary.DAO.Departamento;
import com.library.emaillibrary.DAO.imp.DepartamentoDAOImp;
import com.library.emaillibrary.model.DepartamentoModelo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NuevoDepartamentoController {

    @FXML private TextField txtNombre;

    private Departamento departamentoDAO = new DepartamentoDAOImp();
    private DepartamentoWindowController parentController;

    public void setParentController(DepartamentoWindowController parentController) {
        this.parentController = parentController;
    }

    @FXML
    void onActionRegistrar(ActionEvent event) {
        try {
            if (txtNombre.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "El nombre es obligatorio.");
                return;
            }

            DepartamentoModelo nuevo = new DepartamentoModelo();
            nuevo.setNombre(txtNombre.getText().trim());

            departamentoDAO.insertar(nuevo);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Departamento registrado.");

            // Actualizar la tabla de la ventana padre
            if (parentController != null) {
                parentController.cargarDatos();
            }
            cerrarVentana(event);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
        }
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}