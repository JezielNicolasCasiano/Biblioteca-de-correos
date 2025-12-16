package com.library.emaillibrary.controller.departamento;

import com.library.emaillibrary.DAO.Departamento;
import com.library.emaillibrary.DAO.imp.DepartamentoDAOImp;
import com.library.emaillibrary.model.DepartamentoModelo;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartamentoWindowController implements Initializable {

    @FXML private TableView<DepartamentoModelo> tablaDepartamentos; // Asegúrate que el fx:id en el FXML sea tablaDepartamentos
    @FXML private TableColumn<DepartamentoModelo, Integer> colId;
    @FXML private TableColumn<DepartamentoModelo, String> colNombre;

    private Departamento departamentoDAO = new DepartamentoDAOImp();
    private ObservableList<DepartamentoModelo> listaDepartamentos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaDepartamentos = FXCollections.observableArrayList();

        // Configurar columnas
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdDepartamento()).asObject());
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));

        tablaDepartamentos.setItems(listaDepartamentos);
        cargarDatos();
    }

    public void cargarDatos() {
        try {
            listaDepartamentos.clear();
            listaDepartamentos.addAll(departamentoDAO.listarDepartamento());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los departamentos: " + e.getMessage());
        }
    }

    @FXML
    void onActionEliminar(ActionEvent event) {
        DepartamentoModelo seleccionado = tablaDepartamentos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Selecciona un departamento para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar " + seleccionado.getNombre() + "?");
        confirmacion.setContentText("Esta acción es irreversible.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Intentamos eliminar
                departamentoDAO.eliminar(seleccionado.getIdDepartamento());
                listaDepartamentos.remove(seleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Departamento eliminado.");

            } catch (SQLException e) {
                // CÓDIGO 2292 es Integrity Constraint Violation en Oracle
                if (e.getErrorCode() == 2292) {
                    mostrarAlerta(Alert.AlertType.ERROR, "No se puede eliminar",
                            "No se puede eliminar el departamento '" + seleccionado.getNombre() +
                                    "' porque tiene personas asignadas.\n\n" +
                                    "Por favor, elimina o mueve a las personas de este departamento primero.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error en base de datos: " + e.getMessage());
                }
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onActionNuevo(ActionEvent event) {
        abrirFormulario("/com/library/emaillibrary/formulario-nuevo-departamento.fxml", "Nuevo Departamento", null);
    }

    @FXML
    void onActionEditar(ActionEvent event) {
        DepartamentoModelo seleccionado = tablaDepartamentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Selecciona un departamento para editar.");
            return;
        }
        abrirFormulario("/com/library/emaillibrary/formulario-editar-departamento.fxml", "Editar Departamento", seleccionado);
    }

    // Método auxiliar para abrir ventanas modales
    private void abrirFormulario(String fxmlPath, String titulo, DepartamentoModelo departamentoAEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Lógica para inyectar dependencias según el controlador que carguemos
            if (loader.getController() instanceof NuevoDepartamentoController) {
                ((NuevoDepartamentoController) loader.getController()).setParentController(this);
            } else if (loader.getController() instanceof EditarDepartamentoController) {
                ((EditarDepartamentoController) loader.getController()).setParentController(this);
                ((EditarDepartamentoController) loader.getController()).initData(departamentoAEditar);
            }

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            // Asignar el owner para que la ventana modal bloquee a la ventana actual
            stage.initOwner(tablaDepartamentos.getScene().getWindow());
            stage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void onActionBuscar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-busqueda-departamento.fxml"));
            Parent root = loader.load();

            // Obtenemos el controlador de la ventana de búsqueda
            BusquedaDepartamentoController controller = loader.getController();
            // Le pasamos "this" (este controlador) para que sepa a quién filtrar
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Buscar Departamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tablaDepartamentos.getScene().getWindow());
            stage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de búsqueda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Este método será llamado por el controlador de búsqueda
    public void realizarBusqueda(String nombreBusqueda) {
        try {
            listaDepartamentos.clear();
            if (nombreBusqueda == null || nombreBusqueda.trim().isEmpty()) {
                // Si el campo está vacío, cargamos todo de nuevo
                listaDepartamentos.addAll(departamentoDAO.listarDepartamento());
            } else {
                // Si hay texto, usamos el método buscarPorNombre del DAO
                listaDepartamentos.addAll(departamentoDAO.buscarPorNombre(nombreBusqueda));
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al realizar la búsqueda: " + e.getMessage());
        }
    }
}