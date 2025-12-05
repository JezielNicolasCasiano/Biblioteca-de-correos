package com.library.emaillibrary.model;
import java.util.Date;

public class PersonaModelo {
    private Integer idPersona = null;
    private Integer idSucursal = null;
    private Integer idDepartamento = null;
    private String nombre = null;
    private String apellidoPaterno = null;
    private String apellidoMaterno = null;
    private Date fechaDeNacimiento = null;

    public PersonaModelo() {

    }

    public PersonaModelo(Integer idDepartamento, Integer idSucursal, Integer idPersona, String nombre,
                         String apellidoMaterno, String apellidoPaterno, Date fechaDeNacimiento) {
        this.idDepartamento = idDepartamento;
        this.idSucursal = idSucursal;
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellidoMaterno = apellidoMaterno;
        this.apellidoPaterno = apellidoPaterno;
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public Date getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(Date fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }
}
