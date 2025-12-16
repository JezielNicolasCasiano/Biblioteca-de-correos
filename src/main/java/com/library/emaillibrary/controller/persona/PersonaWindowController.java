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
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PersonaWindowController implements Initializable {

    @FXML private TableView<PersonaModelo> tblPersonas;
    @FXML private TableColumn<PersonaModelo, String> colNombre;
    @FXML private TableColumn<PersonaModelo, String> colPaterno;
    @FXML private TableColumn<PersonaModelo, String> colMaterno;
    @FXML private TableColumn<PersonaModelo, String> colFechaNac;
    @FXML private TableColumn<PersonaModelo, String> colFechaFin;
    // Columna especial para mostrar los N departamentos en una sola celda
    @FXML private TableColumn<PersonaModelo, String> colDepartamentos;

    @FXML private Button btnRegistrar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;

    private final PersonaDAO personaDAO = new PersonaDAOImp();
    private final ObservableList<PersonaModelo> listaPersonas = FXCollections.observableArrayList();

    // Para el modo "Selección" (cuando se abre desde Correo)
    private Consumer<PersonaModelo> onPersonaSeleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatos();
        configurarEventosTabla();
    }

    public void setModoSeleccion(Consumer<PersonaModelo> callback) {
        this.onPersonaSeleccionada = callback;
        // En modo selección, quizás quieras deshabilitar edición/eliminación
        btnEliminar.setDisable(true);
        btnRegistrar.setDisable(true);
        btnEditar.setDisable(true);
    }

    private void configurarEventosTabla() {
        tblPersonas.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                if (onPersonaSeleccionada != null) {
                    PersonaModelo seleccionada = tblPersonas.getSelectionModel().getSelectedItem();
                    if (seleccionada != null) {
                        onPersonaSeleccionada.accept(seleccionada);
                        cerrarVentana();
                    }
                }
            }
        });
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colPaterno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApellidoPaterno()));
        colMaterno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApellidoMaterno()));

        colFechaNac.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaDeNacimiento() != null ? data.getValue().getFechaDeNacimiento().toString() : ""));

        colFechaFin.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaDeFin() != null ? data.getValue().getFechaDeFin().toString() : "Activo"));

        // Lógica para mostrar departamentos concatenados (Ej: "Sistemas, RRHH")
        colDepartamentos.setCellValueFactory(data -> {
            if (data.getValue().getDepartamentos() == null || data.getValue().getDepartamentos().isEmpty()) {
                return new SimpleStringProperty("Sin Asignar");
            }
            String deptos = data.getValue().getDepartamentos().stream()
                    .map(DepartamentoModelo::getNombre)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(deptos);
        });

        tblPersonas.setItems(listaPersonas);
    }

    public void cargarDatos() {
        try {
            // IMPORTANTE: Asegúrate de que tu DAO llene la lista de departamentos dentro de cada persona
            listaPersonas.setAll(personaDAO.listarTodas());
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar personas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void realizarBusqueda(String nombre, String paterno, String materno, LocalDate fNac, LocalDate fFin) {
        try {
            listaPersonas.setAll(personaDAO.buscarPersonas(nombre, paterno, materno, fNac, fFin, null));
        } catch (Exception e) {
            mostrarAlerta("Error", "Búsqueda fallida: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionBuscar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-busqueda-persona.fxml"));
            Parent root = loader.load();

            BusquedaPersonaController controller = loader.getController();
            controller.setBusquedaListener((nom, pat, mat, f1, f2) -> realizarBusqueda(nom, pat, mat, f1, f2));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblPersonas.getScene().getWindow());
            stage.setTitle("Filtrar Personas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void actionRegistrar(ActionEvent event) {
        abrirFormulario(null, "Nueva Persona");
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

    private void abrirFormulario(PersonaModelo persona, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-nuevo-persona.fxml"));
            Parent root = loader.load();

            NuevoPersonaController controller = loader.getController();

            // Pasamos la persona. El controlador del formulario se encarga de marcar los departamentos.
            controller.initAttributes(persona);

            controller.setListener((personaProcesada) -> {
                try {
                    // **NOTA CRÍTICA PARA EL DAO**:
                    // Tu DAO.insertar y DAO.actualizar deben encargarse de guardar en la tabla 'Persona'
                    // Y TAMBIÉN iterar sobre personaProcesada.getDepartamentos() para insertar en 'Persona_Departamento'.

                    if (personaProcesada.getIdPersona() == 0) { // O null según tu modelo
                        personaDAO.insertar(personaProcesada);
                        mostrarAlerta("Éxito", "Persona registrada correctamente.", Alert.AlertType.INFORMATION);
                    } else {
                        personaDAO.actualizar(personaProcesada);
                        mostrarAlerta("Éxito", "Persona actualizada correctamente.", Alert.AlertType.INFORMATION);
                    }
                    cargarDatos();

                } catch (Exception e) {
                    mostrarAlerta("Error BD", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            });

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

    @FXML
    void actionEliminar(ActionEvent event) {
        PersonaModelo seleccionada = tblPersonas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Aviso", "Seleccione una persona.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("¿Eliminar a " + seleccionada.getNombre() + "? Se borrará su historial de departamentos.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                personaDAO.eliminar(seleccionada.getIdPersona()); // La BD hará Cascade Delete en Persona_Departamento
                cargarDatos();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) tblPersonas.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }
}