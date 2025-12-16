package com.library.emaillibrary.controller.correo;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CorreoWindowController implements Initializable {

    @FXML private TableView<CorreoModelo> tblCorreos;
    @FXML private TableColumn<CorreoModelo, String> colId;
    @FXML private TableColumn<CorreoModelo, String> colCorreo;
    @FXML private TableColumn<CorreoModelo, String> colPropietario;

    private final CorreoDAO correoDAO = new CorreoDAOImp();
    private final ObservableList<CorreoModelo> listaCorreos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getIdCorreo())));
        colCorreo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmailCompleto()));
        colPropietario.setCellValueFactory(cell -> {
            if (cell.getValue().getPersona() != null) {
                return new SimpleStringProperty(cell.getValue().getPersona().toString());
            }
            return new SimpleStringProperty("Sin Asignar");
        });
        tblCorreos.setItems(listaCorreos);
    }

    public void cargarDatos() {
        try {
            listaCorreos.setAll(correoDAO.listarTodos());
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar correos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- LÓGICA DE BÚSQUEDA (Manteniendo lo anterior) ---
    public void realizarBusqueda(String parteLocal, String dominio) {
        try {
            List<CorreoModelo> resultados = correoDAO.buscarPorCorreo(parteLocal, dominio);
            listaCorreos.setAll(resultados);
            if (listaCorreos.isEmpty()) {
                mostrarAlerta("Info", "No se encontraron resultados.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Fallo al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionBuscar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-busqueda-correo.fxml"));
            Parent root = loader.load();

            BusquedaCorreoController controller = loader.getController();
            controller.setBusquedaListener((pl, dom) -> this.realizarBusqueda(pl, dom));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblCorreos.getScene().getWindow());
            stage.setTitle("Buscar Correos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir búsqueda.", Alert.AlertType.ERROR);
        }
    }

    // --- LÓGICA DE REGISTRO / EDICIÓN (Nueva Lógica Desacoplada) ---

    @FXML
    void actionRegistrar(ActionEvent event) {
        abrirFormulario(null, "Registrar Nuevo Correo");
    }

    @FXML
    void actionEditar(ActionEvent event) {
        // 1. Obtener el ítem seleccionado de la tabla
        CorreoModelo correoSeleccionado = tblCorreos.getSelectionModel().getSelectedItem();

        if (correoSeleccionado == null) {
            mostrarAlerta("Aviso", "Por favor, seleccione un correo de la lista para editar.", Alert.AlertType.WARNING);
            return;
        }

        // 2. Abrir el formulario pasando el objeto
        abrirFormulario(correoSeleccionado, "Editar Correo Existente");
    }

    /**
     * Método genérico para abrir el formulario (sirve para Crear y Editar)
     */
    private void abrirFormulario(CorreoModelo correo, String tituloVentana) {
        try {
            // IMPORTANTE: Usa el MISMO FXML para registrar y editar.
            // Si usas 'formulario-editar-correo.fxml', asegúrate de que tenga asignado el 'CorreoRegistrarController'
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/emaillibrary/formulario-nuevo-correo.fxml"));
            Parent root = loader.load();

            CorreoRegistrarController controller = loader.getController();

            // AQUI OCURRE LA MAGIA: Pasamos el objeto para que se llenen los campos
            controller.initAttributes(correo);

            // Definimos qué hacer al guardar
            controller.setListener((correoProcesado) -> {
                try {
                    if (correoProcesado.getIdCorreo() != null) {
                        // SI TIENE ID -> ES UNA ACTUALIZACIÓN (UPDATE)
                        correoDAO.actualizar(correoProcesado);
                        mostrarAlerta("Éxito", "El correo se actualizó correctamente.", Alert.AlertType.INFORMATION);
                    } else {
                        // SI NO TIENE ID -> ES UN REGISTRO NUEVO (INSERT)
                        correoDAO.insertar(correoProcesado);
                        mostrarAlerta("Éxito", "El correo se registró correctamente.", Alert.AlertType.INFORMATION);
                    }
                    // Refrescar la tabla para ver los cambios
                    cargarDatos();

                } catch (Exception e) {
                    mostrarAlerta("Error de Base de Datos", "No se pudo guardar los cambios: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(tituloVentana);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error UI", "No se pudo cargar el formulario.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionEliminar(ActionEvent event) {
        CorreoModelo seleccionado = tblCorreos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Aviso", "Seleccione un correo para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("¿Eliminar correo?");
        confirm.setContentText("Se eliminará: " + seleccionado.getEmailCompleto());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                correoDAO.eliminar(seleccionado.getIdCorreo());
                cargarDatos();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.show();
    }



}