package com.library.emaillibrary.controller.persona;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.PersonaDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
import com.library.emaillibrary.DAO.imp.PersonaDAOImp;
import com.library.emaillibrary.controller.correo.CorreoWindowController;
import com.library.emaillibrary.controller.departamento.DepartamentoWindowController; // Asumo que existe o usas Busqueda
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class PersonaRegistrarController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPaterno;
    @FXML private TextField txtMaterno;
    @FXML private DatePicker dpFechaNac;
    @FXML private DatePicker dpFechaFin;

    // Lista para mostrar los departamentos asignados (N:M)
    @FXML private ListView<DepartamentoModelo> listDepartamentos;

    // Campo para mostrar el correo seleccionado (si aplica reasignación)
    @FXML private TextField txtCorreoSeleccionado;

    private PersonaModelo personaEdicion;
    private PersonaWindowController parentController;

    private final PersonaDAO personaDAO = new PersonaDAOImp();
    private final CorreoDAO correoDAO = new CorreoDAOImp();

    private ObservableList<DepartamentoModelo> deptosAsignados = FXCollections.observableArrayList();
    private CorreoModelo correoSeleccionado; // El correo traído de la ventana de búsqueda

    public void initAttributes(PersonaModelo persona, PersonaWindowController parent) {
        this.parentController = parent;
        this.personaEdicion = persona;

        listDepartamentos.setItems(deptosAsignados);

        if (persona != null) {
            // Cargar datos existentes
            txtNombre.setText(persona.getNombre());
            txtPaterno.setText(persona.getApellidoPaterno());
            txtMaterno.setText(persona.getApellidoMaterno());
            dpFechaNac.setValue(persona.getFechaDeNacimiento());
            dpFechaFin.setValue(persona.getFechaDeFin());

            // Cargar departamentos ya asociados
            if (persona.getDepartamentos() != null) {
                deptosAsignados.setAll(persona.getDepartamentos());
            }

            // Cargar correo actual
            if (persona.getCorreo() != null) {
                this.correoSeleccionado = persona.getCorreo();
                txtCorreoSeleccionado.setText(persona.getCorreo().getEmailCompleto());
            }
        }
    }

    @FXML
    void actionBuscarDepartamento(ActionEvent event) {
        // Abre la ventana de Departamentos en modo selección
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-busqueda-departamento.fxml"));
            // OJO: Si prefieres la tabla completa, usa 'departamento-window.fxml'
            Parent root = loader.load();

            // Aquí asumimos que tienes un controlador de búsqueda que permite devolver el objeto
            // Si usas el BusquedaController genérico, asegúrate de añadirle el método 'setPersonaRegistrarController'
            BusquedaDepartamentoController controller = loader.getController();
            controller.setPersonaRegistrarController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Seleccionar Departamento");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Callback para recibir el departamento
    public void recibirDepartamento(DepartamentoModelo depto) {
        if (!deptosAsignados.contains(depto)) {
            deptosAsignados.add(depto);
        }
    }

    @FXML
    void actionBuscarCorreo(ActionEvent event) {
        // Abre la ventana de Correos en modo selección
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/correos-window.fxml"));
            Parent root = loader.load();

            // Reutilizamos la ventana de correos pero necesitamos saber qué seleccionó
            CorreoWindowController controller = loader.getController();
            // Necesitarías implementar un modo de selección en CorreoWindowController
            controller.setModoSeleccion(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Seleccionar Correo (Reasignar)");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Callback para recibir el correo
    public void recibirCorreo(CorreoModelo correo) {
        this.correoSeleccionado = correo;
        txtCorreoSeleccionado.setText(correo.getEmailCompleto());
    }

    @FXML
    void actionGuardar(ActionEvent event) {
        if (txtNombre.getText().isEmpty() || txtPaterno.getText().isEmpty()) {
            mostrarAlerta("Error", "Nombre y Apellido Paterno son obligatorios.");
            return;
        }

        try {
            boolean esNuevo = (personaEdicion == null);
            if (esNuevo) {
                personaEdicion = new PersonaModelo();
            }

            personaEdicion.setNombre(txtNombre.getText());
            personaEdicion.setApellidoPaterno(txtPaterno.getText());
            personaEdicion.setApellidoMaterno(txtMaterno.getText());
            personaEdicion.setFechaDeNacimiento(dpFechaNac.getValue());
            personaEdicion.setFechaDeFin(dpFechaFin.getValue());

            // Asignar los departamentos seleccionados
            personaEdicion.setDepartamentos(deptosAsignados);

            if (esNuevo) {
                personaDAO.insertar(personaEdicion); // Esto guarda Persona y las relaciones en Persona_Departamento
            } else {
                personaDAO.actualizar(personaEdicion);
            }

            // Manejo especial del Correo (1:1)
            // Si seleccionamos un correo existente, lo "traemos" a esta persona.
            if (correoSeleccionado != null) {
                // Actualizamos el dueño del correo al ID de esta persona (recién creada o editada)
                correoSeleccionado.setPersona(personaEdicion);
                correoDAO.actualizar(correoSeleccionado);
            }

            if (parentController != null) parentController.cargarDatos();
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error SQL", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    void actionCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }
}