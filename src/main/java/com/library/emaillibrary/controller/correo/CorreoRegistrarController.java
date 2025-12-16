package com.library.emaillibrary.controller.correo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import com.library.emaillibrary.DAO.*;
import com.library.emaillibrary.model.*;


public class CorreoRegistrarController {

    @FXML private TextField txtCorreo;
    @FXML private TextField txtPersona; // Campo de texto NO editable (solo muestra nombre)
    @FXML private Button btnBuscarPersona;

    private Stage stage;
    private CorreoDAO correoDAO;
    private PersonaModelo personaSeleccionada; // La persona que el usuario elija

    // Inicializamos las dependencias
    public void init(Stage stage, CorreoDAO correoDAO) {
        this.stage = stage;
        this.correoDAO = correoDAO;
        txtPersona.setEditable(false); // Importante: que no escriban el nombre a mano
    }

    @FXML
    private void abrirBusquedaPersona() {
        // TODO: Aquí debes abrir tu ventana de "Lista de Personas" existente.
        // Lo ideal es abrirla como modal y esperar el resultado, o pasarle 'this'
        // para que ella te devuelva la persona seleccionada.

        System.out.println("Abriendo selector de personas...");
        // Cuando recuperes la persona desde tu otra ventana, llámame a este método:
        // setPersonaSeleccionada(personaRecuperada);
    }

    // Este método lo llamarás desde tu ventana de búsqueda de personas al hacer doble click
    public void setPersonaSeleccionada(PersonaModelo persona) {
        this.personaSeleccionada = persona;
        if (persona != null) {
            txtPersona.setText(persona.getNombre() + " " + persona.getApellidoPaterno());
        }
    }

    @FXML
    private void guardar() {
        // 1. Validar que tengamos datos
        if (txtCorreo.getText().isEmpty() || personaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan Datos", "Por favor ingrese un correo y seleccione una persona.");
            return;
        }

        // 2. VALIDACIÓN LÓGICA: Verificar si la persona falleció o está de baja
        // Accedemos directamente al atributo del modelo
        if (personaSeleccionada.getFechaFin() != null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acción Inválida",
                    "No se puede asignar un correo a una persona dada de baja (con Fecha Fin).");
            return;
        }

        try {
            // 3. Crear el objeto Correo
            // Asumo que tu constructor acepta (direccion, id_persona)
            CorreoModelo nuevoCorreo = new CorreoModelo();
            nuevoCorreo.set(txtCorreo.getText());
            nuevoCorreo.setPersona(personaSeleccionada.getId());

            // 4. Intentar guardar en BD
            correoDAO.insertar(nuevoCorreo);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Correo registrado correctamente.");
            stage.close();

        } catch (SQLException e) {
            // 5. MANEJO DE EXCEPCIÓN: Duplicidad (Dos correos para la misma persona)
            if (e.getErrorCode() == 1) { // ORA-00001 (Unique Constraint)
                mostrarAlerta(Alert.AlertType.ERROR, "Correo Duplicado",
                        "Esta persona YA TIENE un correo registrado.\nSolo se permite uno por persona.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al guardar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancelar() {
        if (stage != null) stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}