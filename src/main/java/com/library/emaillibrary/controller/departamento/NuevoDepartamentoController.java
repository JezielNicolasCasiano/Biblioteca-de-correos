package com.library.emaillibrary.controller.departamento;

import com.library.emaillibrary.model.DepartamentoModelo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NuevoDepartamentoController {

    @FXML private TextField txtNombre;

    private DepartamentoModelo departamentoEdicion;

    // Interfaz para comunicarse con el padre
    public interface DepartamentoFormularioListener {
        void onGuardar(DepartamentoModelo departamento);
    }

    private DepartamentoFormularioListener listener;

    public void setListener(DepartamentoFormularioListener listener) {
        this.listener = listener;
    }

    /**
     * Inicializa el formulario.
     * @param departamento Null para nuevo registro, Objeto para editar.
     */
    public void initAttributes(DepartamentoModelo departamento) {
        this.departamentoEdicion = departamento;

        if (departamento != null) {
            // MODO EDICIÓN: Pre-cargar datos
            txtNombre.setText(departamento.getNombre());
        } else {
            // MODO REGISTRO: Limpiar
            txtNombre.clear();
        }
    }

    @FXML
    void actionGuardar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();

        // 1. Validaciones simples
        if (nombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre del departamento no puede estar vacío.");
            return;
        }

        // 2. Preparar el objeto
        if (departamentoEdicion == null) {
            departamentoEdicion = new DepartamentoModelo();
        }

        // Actualizamos el nombre (el ID se conserva si ya existía)
        departamentoEdicion.setNombre(nombre);

        // 3. Enviamos al padre
        if (listener != null) {
            listener.onGuardar(departamentoEdicion);
        }

        cerrarVentana(event);
    }

    @FXML
    void actionCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.show();
    }
}