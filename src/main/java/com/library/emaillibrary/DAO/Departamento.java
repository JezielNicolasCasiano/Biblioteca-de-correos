package com.library.emaillibrary.DAO;
import com.library.emaillibrary.model.DepartamentoModelo;
import java.util.List;

public interface Departamento {
    public List<DepartamentoModelo> listarDepartamento() throws Exception;
}
