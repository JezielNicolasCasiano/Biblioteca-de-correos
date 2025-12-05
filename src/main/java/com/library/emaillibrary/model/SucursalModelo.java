package com.library.emaillibrary.model;

public class SucursalModelo {

    private Integer idSucursal = null;
    private String nombre = null;
    private String ciudad = null;

    public SucursalModelo() {
    }

    public SucursalModelo(String nombre, Integer idSucursal, String ciudad) {
        this.nombre = nombre;
        this.idSucursal = idSucursal;
        this.ciudad = ciudad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
