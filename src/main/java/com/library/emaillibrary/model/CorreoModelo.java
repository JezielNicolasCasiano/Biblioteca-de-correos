package com.library.emaillibrary.model;

public class CorreoModelo {

    private Integer idCorreo = null;
    private Integer idPersona = null;
    private String parteLocal = null;
    private String dominio = null;

    public CorreoModelo() {
    }

    public CorreoModelo(String parteLocal, Integer idPersona, Integer idCorreo, String dominio) {
        this.parteLocal = parteLocal;
        this.idPersona = idPersona;
        this.idCorreo = idCorreo;
        this.dominio = dominio;
    }

    public Integer getIdCorreo() {
        return idCorreo;
    }

    public void setIdCorreo(Integer idCorreo) {
        this.idCorreo = idCorreo;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public String getParteLocal() {
        return parteLocal;
    }

    public void setParteLocal(String parteLocal) {
        this.parteLocal = parteLocal;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }
}


