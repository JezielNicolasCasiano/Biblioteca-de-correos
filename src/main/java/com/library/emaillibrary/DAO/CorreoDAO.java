package com.library.emaillibrary.DAO;

import com.library.emaillibrary.model.CorreoModelo;
import java.time.LocalDate;
import java.util.List;

public interface CorreoDAO {

    List<CorreoModelo> listarCorreos(String nombre, String aPaterno, String aMaterno,
                                     Integer idDepartamento, Integer idSucursal,
                                     String correoBusqueda, LocalDate fechaNac) throws Exception;

    List<CorreoModelo> listarTodos() throws Exception;

    CorreoModelo obtenerPorId(int idCorreo) throws Exception;

    void eliminar(int idCorreo) throws Exception;

    void insertar(CorreoModelo nuevoCorreo) throws Exception;

    void actualizar(CorreoModelo correo) throws Exception;


}