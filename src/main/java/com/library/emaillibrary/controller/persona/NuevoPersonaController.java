package com.library.emaillibrary.controller.persona;

import com.library.emaillibrary.controller.departamento.DepartamentoWindowController;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NuevoPersonaController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPaterno;
    @FXML private TextField txtMaterno;
    @FXML private DatePicker dpFechaNac;
    @FXML private DatePicker dpFechaFin;

    // NUEVOS COMPONENTES EN FXML
    @FXML private Button btnSeleccionarDeptos;
    @FXML private Label lblDeptosSeleccionados; // Para mostrar feedback al usuario (Ej: "3 seleccionados")

    private PersonaModelo personaEdicion;

    // Lista temporal donde guardaremos lo que seleccione en la otra ventana
    private List<DepartamentoModelo> departamentosSeleccionados = new ArrayList<>();

    // Listener para devolver la persona lista al padre
    public interface PersonaFormularioListener {
        void onGuardar(PersonaModelo persona);
    }

    private PersonaFormularioListener listener;

    public void setListener(PersonaFormularioListener listener) {
        this.listener = listener;
    }

    /**
     * Carga los datos de la persona.
     * LOGICA DE EDICIÓN: Los departamentos inician VACÍOS (seleccionamos de 0).
     */
    public void initAttributes(PersonaModelo persona) {
        this.personaEdicion = persona;

        // Siempre reiniciamos la lista de departamentos para obligar a seleccionar de nuevo
        this.departamentosSeleccionados = new ArrayList<>();
        actualizarLabelDepartamentos();

        if (persona != null) {
            // --- MODO EDICIÓN ---
            txtNombre.setText(persona.getNombre());
            txtPaterno.setText(persona.getApellidoPaterno());
            txtMaterno.setText(persona.getApellidoMaterno());
            dpFechaNac.setValue(persona.getFechaDeNacimiento());
            dpFechaFin.setValue(persona.getFechaDeFin());
        } else {
            // --- MODO REGISTRO ---
            txtNombre.clear();
            txtPaterno.clear();
            txtMaterno.clear();
            dpFechaNac.setValue(null);
            dpFechaFin.setValue(null);
        }
    }

    @FXML
    void actionBuscarDepartamentos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/departamento-window.fxml"));
            Parent root = loader.load();

            DepartamentoWindowController controller = loader.getController();

            // Configurar modo selección múltiple
            controller.setModoSeleccionMultiple((listaSeleccionada) -> {
                this.departamentosSeleccionados = new ArrayList<>(listaSeleccionada);
                actualizarLabelDepartamentos();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Seleccionar Departamentos (Ctrl + Clic para varios)");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error UI", "No se pudo abrir la ventana de departamentos.");
        }
    }

    private void actualizarLabelDepartamentos() {
        if (departamentosSeleccionados.isEmpty()) {
            lblDeptosSeleccionados.setText("Ningún departamento seleccionado (Se guardará sin departamentos).");
        } else {
            String nombres = departamentosSeleccionados.stream()
                    .map(DepartamentoModelo::getNombre)
                    .limit(3) // Solo mostramos los primeros 3 nombres para no saturar
                    .collect(Collectors.joining(", "));

            if (departamentosSeleccionados.size() > 3) {
                nombres += "... (+ " + (departamentosSeleccionados.size() - 3) + " más)";
            }
            lblDeptosSeleccionados.setText("Seleccionados: " + nombres);
        }
    }

    @FXML
    void actionGuardar(ActionEvent event) {
        if (txtNombre.getText().isEmpty() || txtPaterno.getText().isEmpty()) {
            mostrarAlerta("Validación", "Nombre y Apellido Paterno son obligatorios.");
            return;
        }

        if (personaEdicion == null) {
            personaEdicion = new PersonaModelo();
        }

        personaEdicion.setNombre(txtNombre.getText().trim());
        personaEdicion.setApellidoPaterno(txtPaterno.getText().trim());
        personaEdicion.setApellidoMaterno(txtMaterno.getText().trim());
        personaEdicion.setFechaDeNacimiento(dpFechaNac.getValue());
        personaEdicion.setFechaDeFin(dpFechaFin.getValue());

        // ASIGNAMOS LA LISTA QUE TRAJIMOS DE LA VENTANA DE DEPARTAMENTOS
        personaEdicion.setDepartamentos(departamentosSeleccionados);

        if (listener != null) {
            listener.onGuardar(personaEdicion);
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