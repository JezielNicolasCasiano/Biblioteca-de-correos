package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.DirectorioDAO;
import com.library.emaillibrary.model.CorreoModelo;
import com.library.emaillibrary.model.DepartamentoModelo;
import com.library.emaillibrary.model.PersonaModelo;
import com.library.emaillibrary.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectorioDAOImp implements DirectorioDAO {

    private static final String SELECT_VISTA =
            "SELECT " +
                    "   p.id_persona, p.nombre, p.apellido_paterno, p.apellido_materno, " +
                    "   p.fecha_de_nacimiento, p.fecha_de_fin, " +
                    "   d.id_departamento, d.nombre AS nom_depto, " +
                    "   c.id_correo, c.parte_local, c.dominio " +
                    "FROM Persona p " +
                    "LEFT JOIN Persona_Departamento pd ON p.id_persona = pd.id_persona " +
                    "LEFT JOIN Departamento d ON pd.id_departamento = d.id_departamento " +
                    "LEFT JOIN Correo c ON p.id_persona = c.id_persona " +
                    "WHERE 1=1 ";

    @Override
    public List<PersonaModelo> listarDirectorioCompleto(String filtroBusqueda) throws Exception {
        StringBuilder sql = new StringBuilder(SELECT_VISTA);

        if (filtroBusqueda != null && !filtroBusqueda.trim().isEmpty()) {
            sql.append(" AND (LOWER(p.nombre) LIKE ? OR LOWER(p.apellido_paterno) LIKE ? OR LOWER(p.apellido_materno) LIKE ?) ");
        }

        sql.append(" ORDER BY p.id_persona DESC");

        Map<Integer, PersonaModelo> mapaPersonas = new HashMap<>();

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (filtroBusqueda != null && !filtroBusqueda.trim().isEmpty()) {
                String busqueda = "%" + filtroBusqueda.trim().toLowerCase() + "%";
                ps.setString(1, busqueda);
                ps.setString(2, busqueda);
                ps.setString(3, busqueda);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPersona = rs.getInt("id_persona");

                    PersonaModelo persona = mapaPersonas.get(idPersona);

                    if (persona == null) {
                        persona = new PersonaModelo();
                        persona.setIdPersona(idPersona);
                        persona.setNombre(rs.getString("nombre"));
                        persona.setApellidoPaterno(rs.getString("apellido_paterno"));
                        persona.setApellidoMaterno(rs.getString("apellido_materno"));

                        if (rs.getDate("fecha_de_nacimiento") != null) {
                            persona.setFechaDeNacimiento(rs.getDate("fecha_de_nacimiento").toLocalDate());
                        }
                        if (rs.getDate("fecha_de_fin") != null) {
                            persona.setFechaDeFin(rs.getDate("fecha_de_fin").toLocalDate());
                        }

                        int idCorreo = rs.getInt("id_correo");
                        if (!rs.wasNull()) {
                            CorreoModelo correo = new CorreoModelo();
                            correo.setIdCorreo(idCorreo);
                            correo.setParteLocal(rs.getString("parte_local"));
                            correo.setDominio(rs.getString("dominio"));
                            correo.setPersona(persona); // Enlace inverso para consistencia

                            persona.setCorreo(correo);
                        }

                        mapaPersonas.put(idPersona, persona);
                    }

                    int idDepto = rs.getInt("id_departamento");
                    if (!rs.wasNull()) {
                        DepartamentoModelo depto = new DepartamentoModelo();
                        depto.setIdDepartamento(idDepto);
                        depto.setNombre(rs.getString("nom_depto"));

                        persona.agregarDepartamento(depto);
                    }
                }
            }
        }
        return new ArrayList<>(mapaPersonas.values());
    }
}