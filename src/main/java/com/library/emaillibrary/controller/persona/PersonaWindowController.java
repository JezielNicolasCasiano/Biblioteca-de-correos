package com.library.emaillibrary.controller.persona;

import com.library.emaillibrary.DAO.PersonaDAO;
import com.library.emaillibrary.DAO.imp.PersonaDAOImp;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PersonaWindowController implements Initializable {

    @FXML private TableView<PersonaModelo> tblPersonas;
    @FXML private TableColumn<PersonaModelo, String> colNombre;
    @FXML private TableColumn<PersonaModelo, String> colPaterno;
    @FXML private TableColumn<PersonaModelo, String> colMaterno;
    @FXML private TableColumn<PersonaModelo, String> colFechaNac;
    @FXML private TableColumn<PersonaModelo, String> colFechaFin;
    @FXML private TableColumn<PersonaModelo, String> colDepartamentos; // Para mostrar lista de deptos

    private final PersonaDAO personaDAO = new PersonaDAOImp();
    private ObservableList<PersonaModelo> listaPersonas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colPaterno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApellidoPaterno()));
        colMaterno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApellidoMaterno()));

        colFechaNac.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaDeNacimiento() != null ? data.getValue().getFechaDeNacimiento().toString() : ""));

        colFechaFin.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaDeFin() != null ? data.getValue().getFechaDeFin().toString() : "Activo"));

        // Mostrar nombres de departamentos separados por coma
        colDepartamentos.setCellValueFactory(data -> {
            String deptos = data.getValue().getDepartamentos().stream()
                    .map(DepartamentoModelo::getNombre)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(deptos);
        });
    }

    public void cargarDatos() {
        try {
            listaPersonas.setAll(personaDAO.listarTodas());
            tblPersonas.setItems(listaPersonas);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar las personas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionRegistrar(ActionEvent event) {
        abrirFormulario(null, "Registrar Persona");
    }

    @FXML
    void actionEditar(ActionEvent event) {
        PersonaModelo seleccionada = tblPersonas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Aviso", "Seleccione una persona para editar.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormulario(seleccionada, "Editar Persona");
    }

    @FXML
    void actionEliminar(ActionEvent event) {
        PersonaModelo seleccionada = tblPersonas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Aviso", "Seleccione una persona para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar a " + seleccionada.getNombre() + "?");
        confirm.setContentText("Esta acción borrará también su correo asociado.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                personaDAO.eliminar(seleccionada.getIdPersona());
                cargarDatos();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void abrirFormulario(PersonaModelo persona, String titulo) {
        try {
            // Usaremos 'formulario-nuevo-persona.fxml' (o editar, son casi iguales)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-nuevo-persona.fxml"));
            Parent root = loader.load();

            PersonaRegistrarController controller = loader.getController();
            controller.initAttributes(persona, this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error UI", "No se pudo abrir el formulario.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }
}