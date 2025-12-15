package com.library.emaillibrary.model;

public class CorreoModelo {

    private Integer idCorreo;
    private String parteLocal;
    private String dominio;
    private PersonaModelo persona;

    public CorreoModelo() {
    }

    public CorreoModelo(Integer idCorreo, String parteLocal, String dominio, PersonaModelo persona) {
        this.idCorreo = idCorreo;
        this.parteLocal = parteLocal;
        this.dominio = dominio;
        this.persona = persona;
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

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public PersonaModelo getPersona() {
        return persona;
    }

    public void setPersona(PersonaModelo persona) {
        this.persona = persona;
    }

    public String getEmailCompleto() {
        return parteLocal + "@" + dominio;
    }
}


