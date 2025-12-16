package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import com.library.emaillibrary.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CorreoDAOImp implements CorreoDAO {

    private static final String SELECT_BASE =
            "SELECT id_correo, id_persona, parte_local, dominio FROM Correo";

    @Override
    public void insertar(CorreoModelo nuevoCorreo) throws Exception {
        String sql = "INSERT INTO Correo (id_persona, parte_local, dominio) VALUES (?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (nuevoCorreo.getPersona() == null || nuevoCorreo.getPersona().getIdPersona() == null) {
                throw new Exception("Error: Debe seleccionar una persona antes de guardar.");
            }

            ps.setInt(1, nuevoCorreo.getPersona().getIdPersona());
            ps.setString(2, nuevoCorreo.getParteLocal());
            ps.setString(3, nuevoCorreo.getDominio());

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(CorreoModelo correo) throws Exception {
        String sql = "UPDATE Correo SET id_persona = ?, parte_local = ?, dominio = ? WHERE id_correo = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, correo.getPersona().getIdPersona());
            ps.setString(2, correo.getParteLocal());
            ps.setString(3, correo.getDominio());
            ps.setInt(4, correo.getIdCorreo());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idCorreo) throws Exception {
        String sql = "DELETE FROM Correo WHERE id_correo = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCorreo);
            ps.executeUpdate();
        }
    }

    @Override
    public CorreoModelo obtenerPorId(int idCorreo) throws Exception {
        String sql = SELECT_BASE + " WHERE id_correo = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCorreo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToCorreo(rs);
            }
        }
        return null;
    }

    @Override
    public List<CorreoModelo> listarTodos() throws Exception {
        String sql = SELECT_BASE + " ORDER BY id_correo DESC";
        List<CorreoModelo> lista = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapResultSetToCorreo(rs));
        }
        return lista;
    }

    @Override
    public List<CorreoModelo> buscarPorCorreo(String parteLocal, String dominio) throws Exception {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (parteLocal != null && !parteLocal.isEmpty()) {
            sql.append(" AND LOWER(parte_local) LIKE ? ");
            params.add("%" + parteLocal.toLowerCase() + "%");
        }
        if (dominio != null && !dominio.isEmpty()) {
            sql.append(" AND LOWER(dominio) LIKE ? ");
            params.add("%" + dominio.toLowerCase() + "%");
        }

        List<CorreoModelo> lista = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i+1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapResultSetToCorreo(rs));
            }
        }
        return lista;
    }

    private CorreoModelo mapResultSetToCorreo(ResultSet rs) throws Exception {
        PersonaModelo p = new PersonaModelo();
        p.setIdPersona(rs.getInt("id_persona"));
        return new CorreoModelo(
                rs.getInt("id_correo"),
                rs.getString("parte_local"),
                rs.getString("dominio"),
                p
        );
    }
}