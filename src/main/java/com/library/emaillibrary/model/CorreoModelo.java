package com.library.emaillibrary.model;

public class CorreoModelo {

    private Integer idCorreo = null;
    private PersonaModelo persona = null;
    private String parteLocal = null;
    private String dominio = null;

    public CorreoModelo(Integer idCorreo, PersonaModelo persona, String parteLocal, String dominio) {
        this.idCorreo = idCorreo;
        this.persona = persona;
        this.parteLocal = parteLocal;
        this.dominio = dominio;
    }

    public Integer getIdCorreo() {
        return idCorreo;
    }

    public void setIdCorreo(Integer idCorreo) {
        this.idCorreo = idCorreo;
    }

    public String getParteLocal() {
        return parteLocal;
    }

    public void setParteLocal(String parteLocal) {
        this.parteLocal = parteLocal;
    }

    public PersonaModelo getPersona() {
        return persona;
    }

    public void setPersona(PersonaModelo persona) {
        this.persona = persona;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }
}


