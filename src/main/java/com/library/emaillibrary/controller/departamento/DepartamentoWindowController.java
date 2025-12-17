package com.library.emaillibrary.controller.departamento;

import com.library.emaillibrary.DAO.DepartamentoDAO;
import com.library.emaillibrary.DAO.imp.DepartamentoDAOImp;
import com.library.emaillibrary.model.DepartamentoModelo;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class DepartamentoWindowController implements Initializable {

    @FXML private TableView<DepartamentoModelo> tblDepartamentos;
    @FXML private TableColumn<DepartamentoModelo, String> colId;
    @FXML private TableColumn<DepartamentoModelo, String> colNombre;

    // Botones para ocultar en modo selección
    @FXML private Button btnRegistrar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private final DepartamentoDAO departamentoDAO = new DepartamentoDAOImp();
    private final ObservableList<DepartamentoModelo> listaDepartamentos = FXCollections.observableArrayList();

    // Callback para devolver MÚLTIPLES departamentos
    private Consumer<List<DepartamentoModelo>> onDepartamentosSeleccionados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatos();
        configurarEventosTabla();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIdDepartamento())));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        tblDepartamentos.setItems(listaDepartamentos);
    }

    private void configurarEventosTabla() {
        // Evento de DOBLE CLIC para confirmar selección
        tblDepartamentos.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                confirmarSeleccionSiAplica();
            }
        });
    }

    /**
     * Configura la ventana para modo "Selección Múltiple".
     */
    public void setModoSeleccionMultiple(Consumer<List<DepartamentoModelo>> callback) {
        this.onDepartamentosSeleccionados = callback;

        // 1. Habilitar selección múltiple en la tabla (Ctrl + Clic / Shift + Clic)
        tblDepartamentos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 2. Ocultar o deshabilitar botones CRUD para evitar confusión
        if (btnRegistrar != null) btnRegistrar.setVisible(false);
        if (btnEditar != null) btnEditar.setVisible(false);
        if (btnEliminar != null) btnEliminar.setVisible(false);
    }

    private void confirmarSeleccionSiAplica() {
        // Verificamos si estamos en modo selección y si hay items seleccionados
        if (onDepartamentosSeleccionados != null) {
            List<DepartamentoModelo> seleccionados = new ArrayList<>(tblDepartamentos.getSelectionModel().getSelectedItems());

            if (!seleccionados.isEmpty()) {
                // Devolvemos la lista al formulario padre
                onDepartamentosSeleccionados.accept(seleccionados);
                cerrarVentana();
            }
        }
    }

    public void cargarDatos() {
        try {
            listaDepartamentos.setAll(departamentoDAO.listarDepartamento());
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los departamentos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void realizarBusqueda(String nombre) {
        try {
            List<DepartamentoModelo> resultados = departamentoDAO.buscarPorNombre(nombre);
            listaDepartamentos.setAll(resultados);
        } catch (Exception e) {
            mostrarAlerta("Error", "Fallo al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionBuscar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-busqueda-departamento.fxml"));
            Parent root = loader.load();
            BusquedaDepartamentoController controller = loader.getController();
            controller.setBusquedaListener(this::realizarBusqueda);
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblDepartamentos.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // -------------------------------------------------------------------------
    // IMPLEMENTACIÓN DE LOS MÉTODOS SOLICITADOS
    // -------------------------------------------------------------------------

    @FXML
    void actionRegistrar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-nuevo-departamento.fxml"));
            Parent root = loader.load();

            // Configurar el controlador si es necesario (ej. pasar referencia para refresh)
            NuevoDepartamentoController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Departamento");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblDepartamentos.getScene().getWindow());
            stage.setScene(new Scene(root));

            // Esperar a que se cierre para actualizar la tabla
            stage.showAndWait();
            cargarDatos();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de registro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionEditar(ActionEvent event) {
        DepartamentoModelo seleccionado = tblDepartamentos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona un departamento para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-nuevo-departamento.fxml"));
            Parent root = loader.load();

            // Obtener controlador y pasar los datos
            NuevoDepartamentoController controller = loader.getController();
            // Asegúrate que NuevoDepartamentoController tenga este método:
            controller.initAttributes(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Departamento");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblDepartamentos.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();
            cargarDatos();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de edición.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionEliminar(ActionEvent event) {
        DepartamentoModelo seleccionado = tblDepartamentos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un departamento para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de eliminar el departamento: " + seleccionado.getNombre() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Asumo que el método en tu DAO se llama 'delete'. Si se llama 'eliminar', ajústalo aquí.
                departamentoDAO.eliminar(seleccionado.getIdDepartamento());
                cargarDatos();
                mostrarAlerta("Éxito", "Departamento eliminado correctamente.", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                // CAPTURA ESPECÍFICA PARA ORACLE ORA-02292 (Integrity constraint violation)
                if (e.getErrorCode() == 2292) {
                    mostrarAlerta("No se puede eliminar",
                            "Este departamento tiene PERSONAS asignadas.\n" +
                                    "Primero debes eliminar o reasignar a todas las personas de este departamento.",
                            Alert.AlertType.ERROR);
                } else {
                    e.printStackTrace();
                    mostrarAlerta("Error de Base de Datos", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Ocurrió un error inesperado al eliminar.", Alert.AlertType.ERROR);
            }
        }
    }

    // -------------------------------------------------------------------------

    private void cerrarVentana() {
        Stage stage = (Stage) tblDepartamentos.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}