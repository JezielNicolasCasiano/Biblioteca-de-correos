package com.library.emaillibrary.controller;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.DAO.Departamento;
import com.library.emaillibrary.DAO.SucursalDAO;
import com.library.emaillibrary.DAO.imp.CorreoDAOImp;
import com.library.emaillibrary.DAO.imp.DepartamentoDAOImp;
import com.library.emaillibrary.DAO.imp.SucursalDAOImp;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.DepartamentoModelo;
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
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class BusquedaController implements Initializable {

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
            List<DepartamentoModelo> deptos = deptoDAO.listarDepartamento();
            cbDepartamento.setItems(FXCollections.observableArrayList(deptos));
            cbDepartamento.setConverter(new StringConverter<>() {
                @Override
                public String toString(DepartamentoModelo d) {
                    return (d == null) ? null : d.getNombre();
                }
                @Override
                public DepartamentoModelo fromString(String s) {
                    return null;
                }
            });


            List<SucursalModelo> sucursales = sucursalDAO.listarSucursal();
            cbSucursal.setItems(FXCollections.observableArrayList(sucursales));

            cbSucursal.setConverter(new StringConverter<>() {
                @Override
                public String toString(SucursalModelo s) {
                    return (s == null) ? null : s.getNombre();
                }
                @Override
                public SucursalModelo fromString(String s) {
                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionBuscar(ActionEvent event) {
        try {

            String nombre = txtNombre.getText();
            String paterno = txtPaterno.getText();
            String materno = txtMaterno.getText();
            String correo = txtCorreo.getText();
            LocalDate fecha = dpFechaNac.getValue();
            Integer idDepto = (cbDepartamento.getValue() != null) ? cbDepartamento.getValue().getIdDepartamento() : null;
            Integer idSucursal = (cbSucursal.getValue() != null) ? cbSucursal.getValue().getIdSucursal() : null;

            List<CorreoModelo> resultados = correoDAO.listarCorreos(nombre, paterno, materno, idDepto, idSucursal, correo, fecha);

            if (mainController != null) {
                mainController.actualizarTablaResultados(resultados);
            }

            cerrarVentana(event);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en búsqueda");
            alert.setContentText("Ocurrió un error al buscar: " + e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }

    @FXML
    void onActionCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}