package com.library.emaillibrary.DAO;

import com.library.emaillibrary.model.PersonaModelo;
import java.util.List;

public interface DirectorioDAO {

    List<PersonaModelo> listarDirectorioCompleto(String filtroBusqueda) throws Exception;

}