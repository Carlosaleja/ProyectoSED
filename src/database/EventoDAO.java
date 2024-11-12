package database;

import models.Evento;

import models.Evento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

	public boolean crearEvento(Evento evento) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "INSERT INTO eventos (titulo, descripcion, categoria, fecha, importancia, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, evento.getTitulo());
        stmt.setString(2, evento.getDescripcion());
        stmt.setString(3, evento.getCategoria());
        stmt.setString(4, evento.getFecha());
        stmt.setString(5, evento.getImportancia());
        stmt.setInt(6, evento.getUsuarioId()); 

        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0; 
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    public List<Evento> obtenerEventos() {
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM eventos");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                eventos.add(new Evento(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        rs.getString("fecha"),
                        rs.getString("importancia"),
                        rs.getInt("usuario_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }



    public void actualizarEvento(Evento evento) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE eventos SET titulo = ?, descripcion = ?, categoria = ?, fecha = ?, importancia = ? WHERE id = ?")) {
            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescripcion());
            stmt.setString(3, evento.getCategoria());
            stmt.setString(4, evento.getFecha());
            stmt.setString(5, evento.getImportancia());
            stmt.setInt(6, evento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
