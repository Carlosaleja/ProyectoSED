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
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO eventos (titulo, descripcion, categoria, fecha, importancia, usuario_id) VALUES (?, ?, ?, ?, ?, ?)")) {
        stmt.setString(1, evento.getTitulo());
        stmt.setString(2, evento.getDescripcion());
        stmt.setString(3, evento.getCategoria());
        stmt.setString(4, evento.getFecha());
        stmt.setString(5, evento.getImportancia());
        stmt.setInt(6, evento.getUsuarioId());
        stmt.executeUpdate();
        return true;
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

    public boolean verificarPermiso(int eventoId, int usuarioId) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT usuario_id FROM eventos WHERE id = ?")) {
        stmt.setInt(1, eventoId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int creadorId = rs.getInt("usuario_id");
            return creadorId == usuarioId || esAdministrador(usuarioId);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}



private boolean esAdministrador(int usuarioId) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT rol FROM usuarios WHERE id = ?")) {
        stmt.setInt(1, usuarioId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return "admin".equals(rs.getString("rol"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
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

public void eliminarEvento(int eventoId) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement("DELETE FROM eventos WHERE id = ?")) {
        stmt.setInt(1, eventoId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


}
