package com.library.emaillibrary.DAO.imp;

import com.library.emaillibrary.DAO.PersonaDAO;
import com.library.emaillibrary.model.PersonaModelo;
import com.library.emaillibrary.util.DataBaseConnection;
import com.library.emaillibrary.model.DepartamentoModelo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAOImp implements PersonaDAO {


    @Override
    public List<PersonaModelo> listarPersonasCompleto() throws Exception {
        List<PersonaModelo> lista = new ArrayList<>();

        String sql = "SELECT " +
                " p.id_persona, p.nombre, p.apellido_paterno, p.apellido_materno, p.fecha_de_nacimiento, " +
                " s.id_sucursal, s.nombre AS nom_sucursal, s.ciudad, " +
                " d.id_departamento, d.nombre AS nom_depto " +
                "FROM Persona p " +
                "LEFT JOIN Sucursal s ON p.id_sucursal = s.id_sucursal " +
                "LEFT JOIN Departamento d ON p.id_departamento = d.id_departamento";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PersonaModelo persona = new PersonaModelo();
                persona.setIdPersona(rs.getInt("id_persona"));
                persona.setNombre(rs.getString("nombre"));
                persona.setApellidoPaterno(rs.getString("apellido_paterno"));
                persona.setApellidoMaterno(rs.getString("apellido_materno"));
                persona.setFechaDeNacimiento(rs.getDate("fecha_de_nacimiento"));

                SucursalModelo sucursal = new SucursalModelo();
                sucursal.setIdSucursal(rs.getInt("id_sucursal"));
                sucursal.setNombre(rs.getString("nom_sucursal"));
                sucursal.setCiudad(rs.getString("ciudad"));
                persona.setSucursal(sucursal);

                DepartamentoModelo departamento = new DepartamentoModelo();
                departamento.setIdDepartamento(rs.getInt("id_departamento"));
                departamento.setNombre(rs.getString("nom_depto"));
                persona.setDepartamento(departamento);

                lista.add(persona);
            }
        }
        return lista;
    }
}
