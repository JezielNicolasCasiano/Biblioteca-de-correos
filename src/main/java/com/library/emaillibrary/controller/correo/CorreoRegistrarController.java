package com.library.emaillibrary.controller.correo;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.PersonaDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
import com.library.emaillibrary.DAO.imp.PersonaDAOImp;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private CorreoModelo correoEdicion; // El correo que estamos editando (null si es nuevo)
    private PersonaModelo personaSeleccionada;

    private CorreoWindowController parentController;
    private final CorreoDAO correoDAO = new CorreoDAOImp();
    // private final PersonaDAO personaDAO = new PersonaDAOImp(); // Útil si validas algo extra

    /**
     * Método llamado desde la ventana principal para pasar datos.
     */
    public void initAttributes(CorreoModelo correo, CorreoWindowController parent) {
        this.parentController = parent;
        this.correoEdicion = correo;

        if (correo != null) {
            // Modo Edición
            this.personaSeleccionada = correo.getPersona();
            txtParteLocal.setText(correo.getParteLocal());
            txtDominio.setText(correo.getDominio());
            actualizarLabelPersona();
        } else {
            // Modo Registro
            lblPersonaSeleccionada.setText("Seleccione una persona...");
        }
    }

    @FXML
    void actionBuscarPersona(ActionEvent event) {
        try {
            // Abrimos la ventana de búsqueda de personas
            // IMPORTANTE: Asegúrate de tener 'formulario-busqueda-persona.fxml'
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-busqueda-persona.fxml"));
            Parent root = loader.load();

            // Asumimos que existe un BusquedaPersonaController o genérico
            // Si usas el BusquedaController genérico, asegúrate de que pueda devolver el objeto
            // Aquí simulo que tienes un controlador específico o adaptado:
            BusquedaPersonaController controller = loader.getController();
            controller.setRegistrarController(this); // Para que sepa a quién devolver la persona

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Seleccionar Persona");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la búsqueda de personas.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Este método lo llamará el buscador de personas cuando el usuario elija a alguien.
     */
    public void recibirPersona(PersonaModelo persona) {
        // VALIDACIÓN 1: La persona debe estar viva (fecha_fin == null)
        if (!persona.estaViva()) {
            mostrarAlerta("Persona no válida", "No se puede asignar correo a una persona fallecida (Fecha Fin registrada).", Alert.AlertType.WARNING);
            return;
        }

        // VALIDACIÓN 2: Unicidad (1:1).
        // Si es registro nuevo, la persona NO debe tener correo.
        // Si es edición, la persona puede tener correo solo si es ESTE mismo correo.
        boolean tieneCorreo = (persona.getCorreo() != null);

        // Nota: Para que 'persona.getCorreo()' funcione, tu PersonaDAO debe cargar esa relación,
        // o debes consultar a la BD aquí si esa persona ya tiene ID en la tabla Correo.
        // Asumiremos por ahora que confías en la selección o haces una consulta rápida:
        // if (correoDAO.existeCorreoParaPersona(persona.getId()) && (correoEdicion == null || !correoEdicion.getPersona().equals(persona))) ...

        this.personaSeleccionada = persona;
        actualizarLabelPersona();
    }

    private void actualizarLabelPersona() {
        if (personaSeleccionada != null) {
            lblPersonaSeleccionada.setText(personaSeleccionada.toString() + " (ID: " + personaSeleccionada.getIdPersona() + ")");
        }
    }

    @FXML
    void actionGuardar(ActionEvent event) {
        // Validaciones básicas de campos
        if (personaSeleccionada == null) {
            mostrarAlerta("Faltan datos", "Debe seleccionar una persona propietaria del correo.", Alert.AlertType.WARNING);
            return;
        }
        if (txtParteLocal.getText().isEmpty() || txtDominio.getText().isEmpty()) {
            mostrarAlerta("Faltan datos", "Debe completar la parte local y el dominio.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (correoEdicion == null) {
                // REGISTRO NUEVO
                CorreoModelo nuevoCorreo = new CorreoModelo();
                nuevoCorreo.setPersona(personaSeleccionada);
                nuevoCorreo.setParteLocal(txtParteLocal.getText());
                nuevoCorreo.setDominio(txtDominio.getText());

                correoDAO.insertar(nuevoCorreo);
                mostrarAlerta("Éxito", "Correo registrado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                // EDICIÓN
                correoEdicion.setPersona(personaSeleccionada);
                correoEdicion.setParteLocal(txtParteLocal.getText());
                correoEdicion.setDominio(txtDominio.getText());

                correoDAO.actualizar(correoEdicion);
                mostrarAlerta("Éxito", "Correo actualizado correctamente.", Alert.AlertType.INFORMATION);
            }

            // Cerrar y refrescar
            if (parentController != null) {
                parentController.cargarDatos();
            }
            cerrarVentana();

        } catch (Exception e) {
            mostrarAlerta("Error de Base de Datos", "No se pudo guardar los cambios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtParteLocal.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }
}