package com.library.emaillibrary.controller.correo;

import com.library.emaillibrary.controller.persona.PersonaWindowController;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CorreoRegistrarController {

    @FXML private TextField txtParteLocal;
    @FXML private TextField txtDominio;
    @FXML private Label lblPersonaSeleccionada;

    // Guardamos referencia al correo que se está editando (si existe)
    private CorreoModelo correoEdicion;
    private PersonaModelo personaSeleccionada;

    // Listener para devolver el objeto modificado al padre
    public interface CorreoFormularioListener {
        void onGuardar(CorreoModelo correo);
    }

    private CorreoFormularioListener listener;

    public void setListener(CorreoFormularioListener listener) {
        this.listener = listener;
    }

    /**
     * Este método es el CORAZÓN de la edición.
     * Recibe el correo seleccionado desde la ventana principal.
     */
    public void initAttributes(CorreoModelo correo) {
        this.correoEdicion = correo;

        if (correo != null) {
            // --- MODO EDICIÓN ---
            // 1. Recuperamos la persona dueña del correo
            this.personaSeleccionada = correo.getPersona();

            // 2. Llenamos los campos con la información actual
            txtParteLocal.setText(correo.getParteLocal());
            txtDominio.setText(correo.getDominio());

            // 3. Actualizamos la etiqueta visual
            actualizarLabelPersona();
        } else {
            // --- MODO REGISTRO ---
            this.personaSeleccionada = null;
            lblPersonaSeleccionada.setText("Seleccione una persona...");
            txtParteLocal.clear();
            txtDominio.clear();
        }
    }

    @FXML
    void actionBuscarPersona(ActionEvent event) {
        // Reutilizamos la lógica de búsqueda que ya definimos
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/persona-window.fxml"));
            Parent root = loader.load();

            PersonaWindowController controller = loader.getController();
            controller.setModoSeleccion((persona) -> {
                this.personaSeleccionada = persona;
                actualizarLabelPersona();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Cambiar Propietario");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actualizarLabelPersona() {
        if (personaSeleccionada != null) {
            lblPersonaSeleccionada.setText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidoPaterno());
        }
    }

    @FXML
    void actionGuardar(ActionEvent event) {
        // Validaciones
        if (personaSeleccionada == null) {
            mostrarAlerta("Error", "Debe asignar una persona.");
            return;
        }
        if (txtParteLocal.getText().trim().isEmpty() || txtDominio.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Los campos no pueden estar vacíos.");
            return;
        }

        // Si es un correo nuevo, instanciamos uno. Si es edición, usamos el existente.
        if (correoEdicion == null) {
            correoEdicion = new CorreoModelo();
        }

        // ACTUALIZAMOS LOS DATOS DEL OBJETO
        // Nota: Si es edición, el ID se mantiene intacto dentro de 'correoEdicion'
        correoEdicion.setParteLocal(txtParteLocal.getText().trim());
        correoEdicion.setDominio(txtDominio.getText().trim());
        correoEdicion.setPersona(personaSeleccionada);

        // Devolvemos el objeto listo a la ventana padre
        if (listener != null) {
            listener.onGuardar(correoEdicion);
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
        alert.setContentText(contenido);
        alert.show();
    }
}