package com.library.emaillibrary.DAO;
import com.library.emaillibrary.model.PersonaModelo;
import java.util.List;

public interface PersonaDAO {

    public List<PersonaModelo> listarPersonasCompleto() throws Exception;

}
