package com.library.emaillibrary.controller;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
import com.library.emaillibrary.model.CorreoModelo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainWindowController implements Initializable {

    @FXML
    private TableView<CorreoModelo> tablaCorreos;
    @FXML
    private TableColumn<CorreoModelo, String> colNombre;
    @FXML
    private TableColumn<CorreoModelo, String> colPaterno;
    @FXML
    private TableColumn<CorreoModelo, String> colMaterno;
    @FXML
    private TableColumn<CorreoModelo, String> colCorreo;
    @FXML
    private TableColumn<CorreoModelo, String> colDepartamento;
    @FXML
    private TableColumn<CorreoModelo, String> colSucursal;

    private CorreoDAO correoDAO = new CorreoDAOImp();
    private ObservableList<CorreoModelo> listaCorreos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaCorreos = FXCollections.observableArrayList();

        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getNombre()));

        colPaterno.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getApellidoPaterno()));

        colMaterno.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getApellidoMaterno()));

        colCorreo.setCellValueFactory(cellData -> {
            CorreoModelo c = cellData.getValue();
            return new SimpleStringProperty(c.getParteLocal() + "@" + c.getDominio());
        });

        colDepartamento.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getDepartamento().getNombre()));

        colSucursal.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getSucursal().getNombre()));

        tablaCorreos.setItems(listaCorreos);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            listaCorreos.clear();
            listaCorreos.addAll(correoDAO.listarTodos());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar los correos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onActionEliminar(ActionEvent event) {
        CorreoModelo seleccionado = tablaCorreos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna selección", "Por favor, selecciona un correo de la lista para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar el correo de " + seleccionado.getPersona().getNombre() + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                correoDAO.eliminar(seleccionado.getIdCorreo());

                listaCorreos.remove(seleccionado);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Registro eliminado correctamente.");

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el registro en la BD.");
                e.printStackTrace();
            }
        }
    }



    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void actualizarTablaResultados(List<CorreoModelo> nuevosResultados) {
        listaCorreos.clear();
        listaCorreos.addAll(nuevosResultados);
    }

    @FXML
    void onActionBuscar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-busqueda.fxml"));
            Parent root = loader.load();

            BusquedaController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Búsqueda");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionNuevo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-nuevo.fxml"));
            Parent root = loader.load();
            NuevoController controller = loader.getController();
            controller.setMainController(this);
            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Correo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tablaCorreos.getScene().getWindow());
            stage.showAndWait();
            cargarDatos();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onActionEditar(ActionEvent event) {
        CorreoModelo seleccionado = tablaCorreos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna selección", "Por favor, selecciona un registro de la tabla para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-editar.fxml"));
            Parent root = loader.load();

            EditarController controller = loader.getController();
            controller.setMainController(this);

            controller.initData(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Correo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tablaCorreos.getScene().getWindow());
            stage.showAndWait();

            cargarDatos();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de edición: " + e.getMessage());
            e.printStackTrace();
        }
    }

}