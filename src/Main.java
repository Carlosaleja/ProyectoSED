import database.DatabaseConnection;
import database.UsuarioDAO;
import models.Usuario;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // 1. Prueba de conexión a la base de datos
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("¡Conexión exitosa a la base de datos!");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            return; // Termina el programa si la conexión falla
        }

        // 2. Pruebas de CRUD con UsuarioDAO
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Crear un usuario de prueba
        Usuario nuevoUsuario = new Usuario("Juan Pérez", "juan@example.com", "contraseña123", "Ingeniería", 21);

        // Registrar el usuario en la base de datos
        if (usuarioDAO.registrarUsuario(nuevoUsuario)) {
            System.out.println("Usuario registrado con éxito.");
        } else {
            System.out.println("Error al registrar el usuario.");
        }

        // Obtener el usuario por correo
        Usuario usuarioObtenido = usuarioDAO.obtenerUsuarioPorCorreo("juan@example.com");
        if (usuarioObtenido != null) {
            System.out.println("Usuario encontrado: " + usuarioObtenido.getNombre() + " (" + usuarioObtenido.getCorreo() + ")");
        } else {
            System.out.println("No se encontró el usuario.");
        }

        // Actualizar los datos del usuario
        nuevoUsuario.setNombre("Juan Actualizado");
        nuevoUsuario.setContraseña("nuevaContraseña456");
        nuevoUsuario.setCarrera("Matemáticas");
        nuevoUsuario.setEdad(22);

        if (usuarioDAO.actualizarUsuario(nuevoUsuario)) {
            System.out.println("Usuario actualizado con éxito.");
        } else {
            System.out.println("Error al actualizar el usuario.");
        }

        // Eliminar el usuario por correo
        if (usuarioDAO.eliminarUsuarioPorCorreo("juan@example.com")) {
            System.out.println("Usuario eliminado con éxito.");
        } else {
            System.out.println("Error al eliminar el usuario.");
        }
    }
}
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("¡Conexión exitosa a la base de datos!");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}
