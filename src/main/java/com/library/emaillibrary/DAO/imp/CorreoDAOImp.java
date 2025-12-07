package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.CorreoDAO;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import com.library.emaillibrary.model.SucursalModelo;
import com.library.emaillibrary.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CorreoDAOImp implements CorreoDAO {

    private static final String SELECT_BASE =
            "SELECT " +
                    "   c.id_correo, c.parte_local, c.dominio, " +
                    "   p.id_persona, p.nombre, p.apellido_paterno, p.apellido_materno, p.fecha_de_nacimiento, " +
                    "   s.id_sucursal, s.nombre AS nom_sucursal, s.ciudad, " +
                    "   d.id_departamento, d.nombre AS nom_depto " +
                    "FROM Correo c " +
                    "JOIN Persona p ON c.id_persona = p.id_persona " +
                    "JOIN Sucursal s ON p.id_sucursal = s.id_sucursal " +
                    "JOIN Departamento d ON p.id_departamento = d.id_departamento ";

    @Override
    public List<CorreoModelo> listarTodos() throws Exception {
        String sql = SELECT_BASE + " ORDER BY c.id_correo DESC";
        List<CorreoModelo> lista = new ArrayList<>();

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToCorreo(rs));
            }
        }
        return lista;
    }

    @Override
    public CorreoModelo obtenerPorId(int idCorreo) throws Exception {
        String sql = SELECT_BASE + " WHERE c.id_correo = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCorreo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCorreo(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<CorreoModelo> listarCorreos(String nombre, String aPaterno, String aMaterno,
                                            Integer idDepartamento, Integer idSucursal,
                                            String correoBusqueda, LocalDate fechaNac) throws Exception {

        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append(" WHERE 1=1 ");

        List<Object> parametros = new ArrayList<>();

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND LOWER(p.nombre) LIKE ? ");
            parametros.add("%" + nombre.toLowerCase() + "%");
        }

        if (aPaterno != null && !aPaterno.trim().isEmpty()) {
            sql.append(" AND LOWER(p.apellido_paterno) LIKE ? ");
            parametros.add("%" + aPaterno.toLowerCase() + "%");
        }

        if (aMaterno != null && !aMaterno.trim().isEmpty()) {
            sql.append(" AND LOWER(p.apellido_materno) LIKE ? ");
            parametros.add("%" + aMaterno.toLowerCase() + "%");
        }

        if (idDepartamento != null && idDepartamento > 0) {
            sql.append(" AND p.id_departamento = ? ");
            parametros.add(idDepartamento);
        }

        if (idSucursal != null && idSucursal > 0) {
            sql.append(" AND p.id_sucursal = ? ");
            parametros.add(idSucursal);
        }

        if (fechaNac != null) {
            sql.append(" AND p.fecha_de_nacimiento = ? ");
            parametros.add(Date.valueOf(fechaNac));
        }

        if (correoBusqueda != null && !correoBusqueda.trim().isEmpty()) {
            sql.append(" AND LOWER(c.parte_local || '@' || c.dominio) LIKE ? ");
            parametros.add("%" + correoBusqueda.toLowerCase() + "%");
        }

        sql.append(" ORDER BY p.apellido_paterno ASC");

        List<CorreoModelo> lista = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToCorreo(rs));
                }
            }
        }
        return lista;
    }

    private CorreoModelo mapResultSetToCorreo(ResultSet rs) throws Exception {

        SucursalModelo sucursal = new SucursalModelo();
        sucursal.setIdSucursal(rs.getInt("id_sucursal"));
        sucursal.setNombre(rs.getString("nom_sucursal"));
        sucursal.setCiudad(rs.getString("ciudad"));

        DepartamentoModelo departamento = new DepartamentoModelo();
        departamento.setIdDepartamento(rs.getInt("id_departamento"));
        departamento.setNombre(rs.getString("nom_depto"));

        PersonaModelo persona = new PersonaModelo();
        persona.setIdPersona(rs.getInt("id_persona"));
        persona.setNombre(rs.getString("nombre"));
        persona.setApellidoPaterno(rs.getString("apellido_paterno"));
        persona.setApellidoMaterno(rs.getString("apellido_materno"));
        persona.setFechaDeNacimiento(rs.getDate("fecha_de_nacimiento"));
        persona.setSucursal(sucursal);
        persona.setDepartamento(departamento);

        return new CorreoModelo(
                rs.getInt("id_correo"),
                persona,
                rs.getString("parte_local"),
                rs.getString("dominio")
        );
    }
}
