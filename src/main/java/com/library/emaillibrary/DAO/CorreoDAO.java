package com.library.emaillibrary.DAO;

import com.library.emaillibrary.model.CorreoModelo;
import java.util.List;

public interface CorreoDAO {
    void insertar(CorreoModelo nuevoCorreo) throws Exception;
    void actualizar(CorreoModelo correo) throws Exception;
    void eliminar(int idCorreo) throws Exception;
    CorreoModelo obtenerPorId(int idCorreo) throws Exception;
    List<CorreoModelo> listarTodos() throws Exception;
    List<CorreoModelo> buscarPorCorreo(String parteLocal, String dominio) throws Exception;
}