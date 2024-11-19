package database;

import models.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO {


    public boolean registrarUsuario(Usuario usuario) {
        String query = "INSERT INTO usuarios (nombre, correo, contraseña, carrera, edad) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getContraseña());
            stmt.setString(4, usuario.getCarrera());
            stmt.setInt(5, usuario.getEdad());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

public Usuario obtenerUsuarioPorCorreo(String correo) {
        String query = "SELECT * FROM usuarios WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        rs.getString("carrera"),
                        rs.getInt("edad"),
			rs.getString("rol")
                );
                usuario.setId(rs.getInt("id"));
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        rs.getString("carrera"),
                        rs.getInt("edad")
                );
                usuario.setId(rs.getInt("id"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }


    public boolean actualizarUsuario(Usuario usuario) {
        String query = "UPDATE usuarios SET nombre = ?, contraseña = ?, carrera = ?, edad = ? WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getContraseña());
            stmt.setString(3, usuario.getCarrera());
            stmt.setInt(4, usuario.getEdad());
            stmt.setString(5, usuario.getCorreo());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUsuarioPorCorreo(String correo) {
        String query = "DELETE FROM usuarios WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, correo);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
