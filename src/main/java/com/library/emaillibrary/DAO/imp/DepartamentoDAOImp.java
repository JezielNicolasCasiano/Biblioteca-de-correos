package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.Departamento; 
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAOImp implements Departamento {

    @Override
    public List<DepartamentoModelo> listarDepartamento() throws Exception {
        List<DepartamentoModelo> lista = new ArrayList<>();
        String sql = "SELECT id_departamento, nombre FROM Departamento";

        try (Connection conn = DataBaseConnection.getConnection(); //
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DepartamentoModelo departamento = new DepartamentoModelo();
                departamento.setIdDepartamento(rs.getInt("id_departamento"));
                departamento.setNombre(rs.getString("nombre"));
                lista.add(departamento);
            }
        }
        return lista;
    }
}