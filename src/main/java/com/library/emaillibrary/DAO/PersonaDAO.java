package com.library.emaillibrary.DAO;

import com.library.emaillibrary.model.PersonaModelo;
import java.time.LocalDate;
import java.util.List;

public interface PersonaDAO {
    void insertar(PersonaModelo persona) throws Exception;
    void actualizar(PersonaModelo persona) throws Exception;
    void eliminar(int idPersona) throws Exception;
    List<PersonaModelo> listarTodas() throws Exception;
    List<PersonaModelo> listarParaSeleccionCorreo() throws Exception;
    List<PersonaModelo> buscarPersonas(String nombre, String aPaterno, String aMaterno,
                                       LocalDate fechaNac, LocalDate fechaFin,
                                       Integer idDepartamento) throws Exception;
}