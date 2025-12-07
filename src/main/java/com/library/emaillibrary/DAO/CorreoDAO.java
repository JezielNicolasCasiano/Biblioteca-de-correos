package com.library.emaillibrary.DAO;

import com.library.emaillibrary.model.CorreoModelo;
import java.time.LocalDate;
import java.util.List;

public interface CorreoDAO {

    // Búsqueda compleja (Filtros)
    List<CorreoModelo> listarCorreos(String nombre, String aPaterno, String aMaterno,
                                     Integer idDepartamento, Integer idSucursal,
                                     String correoBusqueda, LocalDate fechaNac) throws Exception;

    // Búsqueda directa (Listar todos / Default)
    List<CorreoModelo> listarTodos() throws Exception;

    // Búsqueda por ID (Para edición/detalle)
    CorreoModelo obtenerPorId(int idCorreo) throws Exception;
}