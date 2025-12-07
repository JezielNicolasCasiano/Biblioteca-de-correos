package com.library.emaillibrary.DAO;
import com.library.emaillibrary.model.SucursalModelo;
import java.util.List;

public interface SucursalDAO {
    public List<SucursalModelo> listarSucursal() throws Exception;
}
