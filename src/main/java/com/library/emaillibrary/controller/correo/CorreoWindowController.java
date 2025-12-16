package com.library.emaillibrary.controller.correo;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
import com.library.emaillibrary.controller.persona.PersonaRegistrarController;
import com.library.emaillibrary.model.CorreoModelo;
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

public class CorreoWindowController implements Initializable {

    @FXML private TableView<CorreoModelo> tblCorreos;
    @FXML private TableColumn<CorreoModelo, String> colId;
    @FXML private TableColumn<CorreoModelo, String> colCorreo; // Email completo
    @FXML private TableColumn<CorreoModelo, String> colPropietario; // Nombre de la persona

    @FXML private Button btnRegistrar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnBuscar;
    @FXML private TextField txtBusqueda; // Campo para buscar por nombre o correo

    private final CorreoDAO correoDAO = new CorreoDAOImp();
    private ObservableList<CorreoModelo> listaCorreos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getIdCorreo())));

        // Unimos parte local y dominio para mostrar el correo completo
        colCorreo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmailCompleto()));

        // Mostramos el nombre completo del dueño
        colPropietario.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPersona().toString()));
    }

    public void cargarDatos() {
        try {
            listaCorreos.setAll(correoDAO.listarTodos());
            tblCorreos.setItems(listaCorreos);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar correos", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionRegistrar(ActionEvent event) {
        abrirFormulario(null, "Registrar Correo");
    }

    @FXML
    void actionEditar(ActionEvent event) {
        CorreoModelo seleccionado = tblCorreos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selección requerida", "Por favor selecciona un correo para editar.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormulario(seleccionado, "Editar Correo");
    }

    @FXML
    void actionEliminar(ActionEvent event) {
        CorreoModelo seleccionado = tblCorreos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selección requerida", "Por favor selecciona un correo para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar el correo de " + seleccionado.getPersona().getNombre() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                correoDAO.eliminar(seleccionado.getIdCorreo());
                cargarDatos();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void abrirFormulario(CorreoModelo correo, String titulo) {
        try {
            // Nota: Asegúrate de que tu FXML apunta a 'CorreoRegistrarController'
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-nuevo-correo.fxml"));
            Parent root = loader.load();

            CorreoRegistrarController controller = loader.getController();
            controller.initAttributes(correo, this); // Pasamos el correo (o null) y este controlador para refrescar

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            mostrarAlerta("Error de interfaz", "No se pudo abrir el formulario: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }
    private PersonaRegistrarController personaController;

    public void setModoSeleccion(PersonaRegistrarController controller) {
        this.personaController = controller;
        // Opcional: Cambiar texto del botón "Editar" a "Seleccionar"
    }

    // Agregar un botón "Seleccionar" en el FXML o reutilizar uno
    public void actionSeleccionar() {
        CorreoModelo seleccionado = tblCorreos.getSelectionModel().getSelectedItem();
        if (personaController != null && seleccionado != null) {
            personaController.recibirCorreo(seleccionado);
            cerrarVentana();
        }
    }
}