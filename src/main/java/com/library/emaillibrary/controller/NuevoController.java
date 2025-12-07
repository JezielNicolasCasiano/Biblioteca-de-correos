package com.library.emaillibrary.controller;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.Departamento;
import com.library.emaillibrary.DAO.SucursalDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
import com.library.emaillibrary.DAO.imp.DepartamentoDAOImp;
import com.library.emaillibrary.DAO.imp.SucursalDAOImp;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import com.library.emaillibrary.model.SucursalModelo;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NuevoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPaterno;
    @FXML private TextField txtMaterno;
    @FXML private TextField txtCorreo;
    @FXML private DatePicker dpFechaNac;

    @FXML private ChoiceBox<DepartamentoModelo> cbDepartamento;
    @FXML private ChoiceBox<SucursalModelo> cbSucursal;

    private CorreoDAO correoDAO = new CorreoDAOImp();
    private Departamento deptoDAO = new DepartamentoDAOImp();
    private SucursalDAO sucursalDAO = new SucursalDAOImp();

    private MainWindowController mainController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCombos();
    }

    public void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
    }

    private void cargarCombos() {
        try {
            List<DepartamentoModelo> departamento = deptoDAO.listarDepartamento();
            cbDepartamento.setItems(FXCollections.observableArrayList(departamento));
            cbDepartamento.setConverter(new StringConverter<>() {
                @Override public String toString(DepartamentoModelo d) { return (d == null) ? null : d.getNombre(); }
                @Override public DepartamentoModelo fromString(String s) { return null; }
            });

            List<SucursalModelo> sucursales = sucursalDAO.listarSucursal();
            cbSucursal.setItems(FXCollections.observableArrayList(sucursales));
            cbSucursal.setConverter(new StringConverter<>() {
                @Override public String toString(SucursalModelo s) { return (s == null) ? null : s.getNombre(); }
                @Override public SucursalModelo fromString(String s) { return null; }
            });

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar listas: " + e.getMessage());
        }
    }

    @FXML
    void onActionRegistrar(ActionEvent event) {
        try {
            if (camposVacios()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor llena todos los campos obligatorios.");
                return;
            }

            String emailCompleto = txtCorreo.getText().trim();
            if (!emailCompleto.contains("@")) {
                mostrarAlerta(Alert.AlertType.WARNING, "Formato incorrecto", "El correo debe contener un '@'.");
                return;
            }

            String[] partesCorreo = emailCompleto.split("@");
            if (partesCorreo.length != 2) {
                mostrarAlerta(Alert.AlertType.WARNING, "Formato incorrecto", "El formato del correo no es válido.");
                return;
            }
            String parteLocal = partesCorreo[0];
            String dominio = partesCorreo[1];

            PersonaModelo nuevaPersona = new PersonaModelo();
            nuevaPersona.setNombre(txtNombre.getText());
            nuevaPersona.setApellidoPaterno(txtPaterno.getText());
            nuevaPersona.setApellidoMaterno(txtMaterno.getText());
            if (dpFechaNac.getValue() != null) {
                nuevaPersona.setFechaDeNacimiento(java.sql.Date.valueOf(dpFechaNac.getValue()));
            }

            nuevaPersona.setDepartamento(cbDepartamento.getValue());
            nuevaPersona.setSucursal(cbSucursal.getValue());
            CorreoModelo nuevoCorreo = new CorreoModelo(null, nuevaPersona, parteLocal, dominio);
            correoDAO.insertar(nuevoCorreo);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Registro guardado correctamente.");


            cerrarVentana(event);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo registrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onActionCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private boolean camposVacios() {
        return txtNombre.getText().isEmpty() ||
                txtPaterno.getText().isEmpty() ||
                txtCorreo.getText().isEmpty() ||
                cbDepartamento.getValue() == null ||
                cbSucursal.getValue() == null;
    }

    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}