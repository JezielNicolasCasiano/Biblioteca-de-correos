package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.PersonaDAO;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import com.library.emaillibrary.util.DataBaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonaDAOImp implements PersonaDAO {

    private static final String SELECT_PERSONA_DEPTO =
            "SELECT p.id_persona, p.nombre, p.apellido_paterno, p.apellido_materno, " +
                    "p.fecha_de_nacimiento, p.fecha_de_fin, " +
                    "d.id_departamento, d.nombre as nom_depto " +
                    "FROM Persona p " +
                    "LEFT JOIN Persona_Departamento pd ON p.id_persona = pd.id_persona " +
                    "LEFT JOIN Departamento d ON pd.id_departamento = d.id_departamento ";

    @Override
    public List<PersonaModelo> buscarPersonas(String nombre, String aPaterno, String aMaterno,
                                              LocalDate fechaNac, LocalDate fechaFin,
                                              Integer idDepartamento) throws Exception {

        StringBuilder sql = new StringBuilder(SELECT_PERSONA_DEPTO);
        sql.append(" WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND LOWER(p.nombre) LIKE ? ");
            parametros.add("%" + nombre.trim().toLowerCase() + "%");
        }
        if (aPaterno != null && !aPaterno.trim().isEmpty()) {
            sql.append(" AND LOWER(p.apellido_paterno) LIKE ? ");
            parametros.add("%" + aPaterno.trim().toLowerCase() + "%");
        }
        if (aMaterno != null && !aMaterno.trim().isEmpty()) {
            sql.append(" AND LOWER(p.apellido_materno) LIKE ? ");
            parametros.add("%" + aMaterno.trim().toLowerCase() + "%");
        }
        if (fechaNac != null) {
            sql.append(" AND p.fecha_de_nacimiento = ? ");
            parametros.add(fechaNac);
        }
        if (fechaFin != null) {
            sql.append(" AND p.fecha_de_fin = ? ");
            parametros.add(fechaFin);
        }
        if (idDepartamento != null && idDepartamento > 0) {
            sql.append(" AND EXISTS (SELECT 1 FROM Persona_Departamento pd_filter WHERE pd_filter.id_persona = p.id_persona AND pd_filter.id_departamento = ?) ");
            parametros.add(idDepartamento);
        }

        sql.append(" ORDER BY p.apellido_paterno ASC");

        return ejecutarConsulta(sql.toString(), parametros);
    }

    @Override
    public List<PersonaModelo> listarTodas() throws Exception {
        String sql = SELECT_PERSONA_DEPTO + " ORDER BY p.id_persona DESC";
        return ejecutarConsulta(sql, new ArrayList<>());
    }

    @Override
    public List<PersonaModelo> listarParaSeleccionCorreo() throws Exception {
        String sql = SELECT_PERSONA_DEPTO +
                " LEFT JOIN Correo c ON p.id_persona = c.id_persona " +
                " WHERE c.id_correo IS NULL " +
                " ORDER BY p.nombre";
        return ejecutarConsulta(sql, new ArrayList<>());
    }


    @Override
    public void insertar(PersonaModelo persona) throws Exception {
        String sqlPersona = "INSERT INTO Persona (nombre, apellido_paterno, apellido_materno, fecha_de_nacimiento, fecha_de_fin) VALUES (?, ?, ?, ?, ?)";
        String sqlRelacion = "INSERT INTO Persona_Departamento (id_persona, id_departamento) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlPersona, new String[]{"id_persona"})) {
                ps.setString(1, persona.getNombre());
                ps.setString(2, persona.getApellidoPaterno());
                ps.setString(3, persona.getApellidoMaterno());
                ps.setObject(4, persona.getFechaDeNacimiento());
                ps.setObject(5, persona.getFechaDeFin());

                if (ps.executeUpdate() == 0) throw new SQLException("Fallo al insertar persona");

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) persona.setIdPersona(rs.getInt(1));
                }
            }

            if (persona.getDepartamentos() != null && !persona.getDepartamentos().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlRelacion)) {
                    for (DepartamentoModelo depto : persona.getDepartamentos()) {
                        ps.setInt(1, persona.getIdPersona());
                        ps.setInt(2, depto.getIdDepartamento());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }

    @Override
    public void actualizar(PersonaModelo persona) throws Exception {
        String sqlUpdate = "UPDATE Persona SET nombre=?, apellido_paterno=?, apellido_materno=?, fecha_de_nacimiento=?, fecha_de_fin=? WHERE id_persona=?";
        String sqlDeleteRel = "DELETE FROM Persona_Departamento WHERE id_persona=?";
        String sqlInsertRel = "INSERT INTO Persona_Departamento (id_persona, id_departamento) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, persona.getNombre());
                ps.setString(2, persona.getApellidoPaterno());
                ps.setString(3, persona.getApellidoMaterno());
                ps.setObject(4, persona.getFechaDeNacimiento());
                ps.setObject(5, persona.getFechaDeFin());
                ps.setInt(6, persona.getIdPersona());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteRel)) {
                ps.setInt(1, persona.getIdPersona());
                ps.executeUpdate();
            }
            if (persona.getDepartamentos() != null && !persona.getDepartamentos().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertRel)) {
                    for (DepartamentoModelo depto : persona.getDepartamentos()) {
                        ps.setInt(1, persona.getIdPersona());
                        ps.setInt(2, depto.getIdDepartamento());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }

    @Override
    public void eliminar(int idPersona) throws Exception {
        String sql = "DELETE FROM Persona WHERE id_persona = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.executeUpdate();
        }
    }

    private List<PersonaModelo> ejecutarConsulta(String sql, List<Object> params) throws Exception {
        Map<Integer, PersonaModelo> map = new HashMap<>();

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_persona");
                    PersonaModelo p = map.get(id);

                    if (p == null) {
                        p = new PersonaModelo();
                        p.setIdPersona(id);
                        p.setNombre(rs.getString("nombre"));
                        p.setApellidoPaterno(rs.getString("apellido_paterno"));
                        p.setApellidoMaterno(rs.getString("apellido_materno"));
                        if(rs.getDate("fecha_de_nacimiento") != null)
                            p.setFechaDeNacimiento(rs.getDate("fecha_de_nacimiento").toLocalDate());
                        if(rs.getDate("fecha_de_fin") != null)
                            p.setFechaDeFin(rs.getDate("fecha_de_fin").toLocalDate());

                        map.put(id, p);
                    }

                    int idDepto = rs.getInt("id_departamento");
                    if (!rs.wasNull()) {
                        DepartamentoModelo d = new DepartamentoModelo();
                        d.setIdDepartamento(idDepto);
                        d.setNombre(rs.getString("nom_depto"));
                        p.agregarDepartamento(d);
                    }
                }
            }
        }
        return new ArrayList<>(map.values());
    }
}