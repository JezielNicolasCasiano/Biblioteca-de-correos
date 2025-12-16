package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.DepartamentoDAO;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAOImp implements DepartamentoDAO {

    @Override
    public List<DepartamentoModelo> listarDepartamento() throws Exception {
        List<DepartamentoModelo> lista = new ArrayList<>();
        String sql = "SELECT id_departamento, nombre FROM Departamento ORDER BY nombre";

        try (Connection conn = DataBaseConnection.getConnection();
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

    @Override
    public void insertar(DepartamentoModelo departamento) throws Exception {
        String sql = "INSERT INTO Departamento (nombre) VALUES (?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, departamento.getNombre());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(DepartamentoModelo departamento) throws Exception {
        String sql = "UPDATE Departamento SET nombre = ? WHERE id_departamento = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, departamento.getNombre());
            ps.setInt(2, departamento.getIdDepartamento());
            ps.executeUpdate();
        }
    }


    @Override
    public void eliminar(int idDepartamento) throws Exception {
        String sql = "DELETE FROM Departamento WHERE id_departamento = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDepartamento);
            ps.executeUpdate();
        }
    }

    @Override
    public List<DepartamentoModelo> buscarPorNombre(String nombre) throws Exception {
        List<DepartamentoModelo> lista = new ArrayList<>();
        // Usamos LOWER y LIKE para búsqueda insensible a mayúsculas/minúsculas
        String sql = "SELECT id_departamento, nombre FROM Departamento WHERE LOWER(nombre) LIKE ? ORDER BY nombre";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Agregamos los comodines % para que busque coincidencias parciales
            ps.setString(1, "%" + nombre.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartamentoModelo departamento = new DepartamentoModelo();
                    departamento.setIdDepartamento(rs.getInt("id_departamento"));
                    departamento.setNombre(rs.getString("nombre"));
                    lista.add(departamento);
                }
            }
        }
        return lista;
    }
}