package com.library.emaillibrary.controller;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.Departamento;
import com.library.emaillibrary.DAO.SucursalDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
import com.library.emaillibrary.DAO.imp.DepartamentoDAOImp;
import com.library.emaillibrary.DAO.imp.SucursalDAOImp;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.DepartamentoModelo;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class EditarController implements Initializable {

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
    private CorreoModelo correoEdicion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCombos();
    }

    public void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
    }

    public void initData(CorreoModelo correo) {
        this.correoEdicion = correo;

        txtNombre.setText(correo.getPersona().getNombre());
        txtPaterno.setText(correo.getPersona().getApellidoPaterno());
        txtMaterno.setText(correo.getPersona().getApellidoMaterno());

        txtCorreo.setText(correo.getParteLocal() + "@" + correo.getDominio());

        if (correo.getPersona().getFechaDeNacimiento() != null) {
            Date sqlDate = (Date) correo.getPersona().getFechaDeNacimiento();
            dpFechaNac.setValue(sqlDate.toLocalDate());
        }

        int idDeptoActual = correo.getPersona().getDepartamento().getIdDepartamento();
        int idSucursalActual = correo.getPersona().getSucursal().getIdSucursal();

        cbDepartamento.getItems().stream()
                .filter(d -> d.getIdDepartamento() == idDeptoActual)
                .findFirst()
                .ifPresent(d -> cbDepartamento.setValue(d));

        cbSucursal.getItems().stream()
                .filter(s -> s.getIdSucursal() == idSucursalActual)
                .findFirst()
                .ifPresent(s -> cbSucursal.setValue(s));
    }

    private void cargarCombos() {
        try {
            List<DepartamentoModelo> deptos = deptoDAO.listarDepartamento();
            cbDepartamento.setItems(FXCollections.observableArrayList(deptos));
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
    void onActionEditar(ActionEvent event) {
        try {
            if (camposVacios()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor llena todos los campos.");
                return;
            }

            String emailCompleto = txtCorreo.getText().trim();
            if (!emailCompleto.contains("@")) {
                mostrarAlerta(Alert.AlertType.WARNING, "Formato incorrecto", "El correo debe contener un '@'.");
                return;
            }
            String[] partes = emailCompleto.split("@");
            if (partes.length != 2) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error", "Formato de correo inválido.");
                return;
            }

            correoEdicion.setParteLocal(partes[0]);
            correoEdicion.setDominio(partes[1]);

            correoEdicion.getPersona().setNombre(txtNombre.getText());
            correoEdicion.getPersona().setApellidoPaterno(txtPaterno.getText());
            correoEdicion.getPersona().setApellidoMaterno(txtMaterno.getText());
            correoEdicion.getPersona().setDepartamento(cbDepartamento.getValue());
            correoEdicion.getPersona().setSucursal(cbSucursal.getValue());

            if (dpFechaNac.getValue() != null) {
                correoEdicion.getPersona().setFechaDeNacimiento(java.sql.Date.valueOf(dpFechaNac.getValue()));
            }

            correoDAO.actualizar(correoEdicion);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Correo actualizado correctamente.");
            cerrarVentana(event);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar: " + e.getMessage());
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