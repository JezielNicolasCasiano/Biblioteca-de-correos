package com.library.emaillibrary.model;
import java.util.Date;

public class PersonaModelo {
    private Integer idPersona = null;
    private SucursalModelo sucursal = null;
    private DepartamentoModelo departamento = null;
    private String nombre = null;
    private String apellidoPaterno = null;
    private String apellidoMaterno = null;
    private Date fechaDeNacimiento = null;

    public PersonaModelo() {
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public SucursalModelo getSucursal() {
        return sucursal;
    }

    public void setSucursal(SucursalModelo sucursal) {
        this.sucursal = sucursal;
    }

    public DepartamentoModelo getDepartamento() {
        return departamento;
    }

    public void setDepartamento(DepartamentoModelo departamento) {
        this.departamento = departamento;
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
