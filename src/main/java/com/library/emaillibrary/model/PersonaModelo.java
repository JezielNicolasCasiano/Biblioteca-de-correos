package com.library.emaillibrary.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonaModelo {
    private Integer idPersona;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaDeNacimiento;
    private LocalDate fechaDeFin;
    private List<DepartamentoModelo> departamentos = new ArrayList<>();
    private CorreoModelo correo;

    public PersonaModelo() {
    }

    public PersonaModelo(Integer idPersona, String nombre, String apellidoPaterno,
                         String apellidoMaterno, LocalDate fechaDeNacimiento, LocalDate fechaDeFin) {
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.fechaDeFin = fechaDeFin;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
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

    public LocalDate getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(LocalDate fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public LocalDate getFechaDeFin() {
        return fechaDeFin;
    }

    public void setFechaDeFin(LocalDate fechaDeFin) {
        this.fechaDeFin = fechaDeFin;
    }

    public List<DepartamentoModelo> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<DepartamentoModelo> departamentos) {
        this.departamentos = departamentos;
    }

    public void agregarDepartamento(DepartamentoModelo depto) {
        if (this.departamentos == null) {
            this.departamentos = new ArrayList<>();
        }
        this.departamentos.add(depto);
    }

    public CorreoModelo getCorreo() {
        return correo;
    }

    public void setCorreo(CorreoModelo correo) {
        this.correo = correo;
    }
}