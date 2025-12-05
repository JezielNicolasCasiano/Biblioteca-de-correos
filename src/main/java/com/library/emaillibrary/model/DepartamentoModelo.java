package com.library.emaillibrary.model;

public class DepartamentoModelo {

    private Integer idDepartamento = null;
    private String nombre = null;

    public DepartamentoModelo() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

}
