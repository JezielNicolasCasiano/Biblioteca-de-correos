package com.library.emaillibrary.DAO;

import com.library.emaillibrary.model.DepartamentoModelo;
import java.util.List;

public interface DepartamentoDAO {
    List<DepartamentoModelo> listarDepartamento() throws Exception;

    void insertar(DepartamentoModelo departamento) throws Exception;

    void actualizar(DepartamentoModelo departamento) throws Exception;

    void eliminar(int idDepartamento) throws Exception;

    List<DepartamentoModelo> buscarPorNombre(String nombre) throws Exception;
}