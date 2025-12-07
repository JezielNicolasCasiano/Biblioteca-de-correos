package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.model.SucursalModelo;
import com.library.emaillibrary.DAO.SucursalDAO;
import com.library.emaillibrary.util.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAOImp implements SucursalDAO {
    @Override
    public List<SucursalModelo> listarSucursal() throws Exception {
        List<SucursalModelo> lista = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre, ciudad FROM Sucursal";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String nombre = rs.getString("nombre");
                int id = rs.getInt("id_sucursal");
                String ciudad = rs.getString("ciudad");

                SucursalModelo sucursal = new SucursalModelo(nombre, id, ciudad);
                lista.add(sucursal);
            }
        }
        return lista;
    }
}
