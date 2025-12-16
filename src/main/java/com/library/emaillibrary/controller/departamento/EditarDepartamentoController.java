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

public class EditarDepartamentoController {

    @FXML private TextField txtNombre;

    private Departamento departamentoDAO = new DepartamentoDAOImp();
    private DepartamentoWindowController parentController;
    private DepartamentoModelo departamentoEdicion;

    public void setParentController(DepartamentoWindowController parentController) {
        this.parentController = parentController;
    }

    public void initData(DepartamentoModelo departamento) {
        this.departamentoEdicion = departamento;
        txtNombre.setText(departamento.getNombre());
    }

    @FXML
    void onActionEditar(ActionEvent event) { // Este método se conecta al botón "Editar" o "Guardar"
        try {
            if (txtNombre.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "El nombre no puede estar vacío.");
                return;
            }

            departamentoEdicion.setNombre(txtNombre.getText().trim());

            departamentoDAO.actualizar(departamentoEdicion);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Departamento actualizado.");

            if (parentController != null) {
                parentController.cargarDatos();
            }
            cerrarVentana(event);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar: " + e.getMessage());
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