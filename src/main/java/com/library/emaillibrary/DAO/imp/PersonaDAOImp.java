package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.PersonaDAO;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import com.library.emaillibrary.util.DataBaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAOImp implements PersonaDAO {

    /**
     * Inserta una persona y sus relaciones con departamentos en una sola transacción.
     */
    @Override
    public void insertar(PersonaModelo persona) throws SQLException {
        String sqlPersona = "INSERT INTO Persona (nombre, apellido_paterno, apellido_materno, fecha_de_nacimiento, fecha_de_fin) " +
                "VALUES (?, ?, ?, ?, ?)";

        String sqlRelacion = "INSERT INTO Persona_Departamento (id_persona, id_departamento) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement psPersona = null;
        PreparedStatement psRelacion = null;
        ResultSet rsKeys = null;

        try {
            conn = DataBaseConnection.getConnection();
            // 1. INICIAR TRANSACCIÓN (Para que todo se guarde o nada se guarde)
            conn.setAutoCommit(false);

            // 2. INSERTAR DATOS DE LA PERSONA
            psPersona = conn.prepareStatement(sqlPersona, new String[]{"id_persona"}); // Pedimos que nos devuelva el ID generado
            psPersona.setString(1, persona.getNombre());
            psPersona.setString(2, persona.getApellidoPaterno());
            psPersona.setString(3, persona.getApellidoMaterno());

            // Manejo de fechas nulas
            if (persona.getFechaDeNacimiento() != null)
                psPersona.setDate(4, Date.valueOf(persona.getFechaDeNacimiento()));
            else
                psPersona.setNull(4, Types.DATE);

            if (persona.getFechaDeFin() != null)
                psPersona.setDate(5, Date.valueOf(persona.getFechaDeFin()));
            else
                psPersona.setNull(5, Types.DATE);

            int affectedRows = psPersona.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear la persona, no se insertaron filas.");
            }

            // 3. RECUPERAR EL ID GENERADO POR ORACLE
            rsKeys = psPersona.getGeneratedKeys();
            int idPersonaGenerado = 0;
            if (rsKeys.next()) {
                idPersonaGenerado = rsKeys.getInt(1);
                persona.setIdPersona(idPersonaGenerado); // Actualizamos el objeto
            } else {
                throw new SQLException("Error al crear la persona, no se obtuvo el ID.");
            }

            // 4. INSERTAR LOS DEPARTAMENTOS ASOCIADOS (Tabla Intermedia)
            if (persona.getDepartamentos() != null && !persona.getDepartamentos().isEmpty()) {
                psRelacion = conn.prepareStatement(sqlRelacion);
                for (DepartamentoModelo depto : persona.getDepartamentos()) {
                    psRelacion.setInt(1, idPersonaGenerado);
                    psRelacion.setInt(2, depto.getIdDepartamento());
                    psRelacion.addBatch(); // Añadimos al lote
                }
                psRelacion.executeBatch(); // Ejecutamos todas las inserciones juntas
            }

            // 5. CONFIRMAR TRANSACCIÓN
            conn.commit();

        } catch (SQLException e) {
            // SI ALGO FALLA, DESHACEMOS TODO
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            // Restaurar estado y cerrar recursos
            if (rsKeys != null) rsKeys.close();
            if (psPersona != null) psPersona.close();
            if (psRelacion != null) psRelacion.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public void actualizar(PersonaModelo persona) throws SQLException {
        String sqlUpdate = "UPDATE Persona SET nombre=?, apellido_paterno=?, apellido_materno=?, fecha_de_nacimiento=?, fecha_de_fin=? WHERE id_persona=?";
        String sqlDeleteRel = "DELETE FROM Persona_Departamento WHERE id_persona=?"; // Borrar relaciones viejas
        String sqlInsertRel = "INSERT INTO Persona_Departamento (id_persona, id_departamento) VALUES (?, ?)"; // Insertar nuevas

        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // TRANSACCIÓN

            // 1. ACTUALIZAR DATOS BÁSICOS
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, persona.getNombre());
                ps.setString(2, persona.getApellidoPaterno());
                ps.setString(3, persona.getApellidoMaterno());

                if (persona.getFechaDeNacimiento() != null) ps.setDate(4, Date.valueOf(persona.getFechaDeNacimiento()));
                else ps.setNull(4, Types.DATE);

                if (persona.getFechaDeFin() != null) ps.setDate(5, Date.valueOf(persona.getFechaDeFin()));
                else ps.setNull(5, Types.DATE);

                ps.setInt(6, persona.getIdPersona());
                ps.executeUpdate();
            }

            // 2. BORRAR RELACIONES EXISTENTES DE DEPARTAMENTOS
            // Como vamos a reescribir la lista ("seleccionar de 0" al editar), borramos todo lo previo
            try (PreparedStatement psDel = conn.prepareStatement(sqlDeleteRel)) {
                psDel.setInt(1, persona.getIdPersona());
                psDel.executeUpdate();
            }

            // 3. INSERTAR LAS NUEVAS RELACIONES
            if (persona.getDepartamentos() != null && !persona.getDepartamentos().isEmpty()) {
                try (PreparedStatement psIns = conn.prepareStatement(sqlInsertRel)) {
                    for (DepartamentoModelo depto : persona.getDepartamentos()) {
                        psIns.setInt(1, persona.getIdPersona());
                        psIns.setInt(2, depto.getIdDepartamento());
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public void eliminar(int idPersona) throws SQLException {
        // GRACIAS AL "ON DELETE CASCADE" EN TU BASE DE DATOS,
        // Solo necesitamos borrar de la tabla Persona. Oracle borrará las relaciones y correos automáticamente.
        String sql = "DELETE FROM Persona WHERE id_persona = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.executeUpdate();
        }
    }

    @Override
    public List<PersonaModelo> listarTodas() throws Exception {
        List<PersonaModelo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Persona ORDER BY id_persona";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PersonaModelo p = mapearPersona(rs);

                // CRUCIAL: Cargar los departamentos para cada persona
                p.setDepartamentos(obtenerDepartamentosPorPersona(p.getIdPersona(), conn));

                lista.add(p);
            }
        }
        return lista;
    }

    @Override
    public List<PersonaModelo> buscarPersonas(String nombre, String paterno, String materno, LocalDate fechaNac, LocalDate fechaFin, String unused) throws Exception {
        List<PersonaModelo> lista = new ArrayList<>();

        // Construcción dinámica de la consulta
        StringBuilder sql = new StringBuilder("SELECT * FROM Persona WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (nombre != null && !nombre.isEmpty()) {
            sql.append(" AND LOWER(nombre) LIKE ?");
            parametros.add("%" + nombre.toLowerCase() + "%");
        }
        if (paterno != null && !paterno.isEmpty()) {
            sql.append(" AND LOWER(apellido_paterno) LIKE ?");
            parametros.add("%" + paterno.toLowerCase() + "%");
        }
        if (materno != null && !materno.isEmpty()) {
            sql.append(" AND LOWER(apellido_materno) LIKE ?");
            parametros.add("%" + materno.toLowerCase() + "%");
        }
        if (fechaNac != null) {
            sql.append(" AND fecha_de_nacimiento >= ?");
            parametros.add(Date.valueOf(fechaNac));
        }
        if (fechaFin != null) {
            sql.append(" AND fecha_de_nacimiento <= ?"); // Búsqueda por rango de nacimiento
            parametros.add(Date.valueOf(fechaFin));
        }

        sql.append(" ORDER BY apellido_paterno");

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Asignar parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof String) ps.setString(i + 1, (String) param);
                else if (param instanceof Date) ps.setDate(i + 1, (Date) param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PersonaModelo p = mapearPersona(rs);
                    // Cargar sus departamentos
                    p.setDepartamentos(obtenerDepartamentosPorPersona(p.getIdPersona(), conn));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    // --- MÉTODOS AUXILIARES ---

    private PersonaModelo mapearPersona(ResultSet rs) throws SQLException {
        PersonaModelo p = new PersonaModelo();
        p.setIdPersona(rs.getInt("id_persona"));
        p.setNombre(rs.getString("nombre"));
        p.setApellidoPaterno(rs.getString("apellido_paterno"));
        p.setApellidoMaterno(rs.getString("apellido_materno"));

        Date fechaNac = rs.getDate("fecha_de_nacimiento");
        if (fechaNac != null) p.setFechaDeNacimiento(fechaNac.toLocalDate());

        Date fechaFin = rs.getDate("fecha_de_fin");
        if (fechaFin != null) p.setFechaDeFin(fechaFin.toLocalDate());

        return p;
    }

    /**
     * Método auxiliar para traer los departamentos de una persona específica.
     * Realiza un JOIN entre Departamento y la tabla intermedia.
     */
    private List<DepartamentoModelo> obtenerDepartamentosPorPersona(int idPersona, Connection conn) throws SQLException {
        List<DepartamentoModelo> deptos = new ArrayList<>();
        String sql = "SELECT d.id_departamento, d.nombre " +
                "FROM Departamento d " +
                "JOIN Persona_Departamento pd ON d.id_departamento = pd.id_departamento " +
                "WHERE pd.id_persona = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartamentoModelo d = new DepartamentoModelo();
                    d.setIdDepartamento(rs.getInt("id_departamento"));
                    d.setNombre(rs.getString("nombre"));
                    deptos.add(d);
                }
            }
        }
        return deptos;
    }
}