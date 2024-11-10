import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import database.UsuarioDAO;
import models.Usuario;
import java.util.List;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Servidor {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Contexto para la API de usuarios
        server.createContext("/api/usuarios/registro", new RegistroHandler());
        server.createContext("/api/usuarios/login", new LoginHandler());
        server.createContext("/api/usuarios", new UsuariosHandler());

        // Contexto para archivos estáticos (frontend)
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        System.out.println("Servidor iniciado en el puerto 8080");
    }
}


class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();
        String filePath;

        // Verifica si la solicitud es para 'static' o 'templates'
        if (uriPath.startsWith("/static/")) {
            // Si es un archivo estático, lo busca en la carpeta 'static'
            filePath = "static" + uriPath.substring("/static".length());
        } else {
            // Si no es 'static', asume que es un archivo HTML en 'templates'
            filePath = "templates" + uriPath;
            // Si no se especifica archivo, carga 'index.html' por defecto
            if (filePath.equals("templates/") || filePath.endsWith("/")) {
                filePath += "index.html";
            }
        }

        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            // Configura el tipo de contenido basado en la extensión del archivo
            if (filePath.endsWith(".html")) {
                exchange.getResponseHeaders().set("Content-Type", "text/html");
            } else if (filePath.endsWith(".css")) {
                exchange.getResponseHeaders().set("Content-Type", "text/css");
            } else if (filePath.endsWith(".js")) {
                exchange.getResponseHeaders().set("Content-Type", "application/javascript");
            }

            // Lee el archivo y envíalo como respuesta
            byte[] fileData = new FileInputStream(file).readAllBytes();
            exchange.sendResponseHeaders(200, fileData.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileData);
            os.close();
        } else {
            // Si el archivo no existe, responde con 404
            exchange.sendResponseHeaders(404, -1);
        }
    }
}




// Handler para obtener todos los usuarios
class UsuariosHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Usuario> usuarios = new ArrayList<>();
            Gson gson = new Gson();
            OutputStream os = exchange.getResponseBody();  // Definir OutputStream aquí

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT nombre, correo, carrera, edad FROM usuarios";
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    Usuario usuario = new Usuario(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        "",                     // Contraseña vacía o asigna un valor si es necesario
                        rs.getString("carrera"),
                        rs.getInt("edad")
                    );
                    usuarios.add(usuario);
                }

                // Convertir lista de usuarios a JSON
                String jsonResponse = gson.toJson(usuarios);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, 0); // Usar 0 en lugar de -1 para longitud desconocida

                // Escribir respuesta en el OutputStream
                os.write(jsonResponse.getBytes());

            } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(500, 0);  // Código de error 500 para indicar fallo en el servidor
                os.write("Error del servidor".getBytes());
            } finally {
                os.close();  // Cierra el OutputStream en el bloque finally
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Método no permitido, -1 es correcto aquí ya que no se devuelve contenido
        }
    }
}



class RegistroHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Leer los datos JSON del cuerpo de la solicitud
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            // Extraer los campos del JSON
            String nombre = jsonObject.get("nombre").getAsString();
            String correo = jsonObject.get("correo").getAsString();
            String contraseña = jsonObject.get("contraseña").getAsString();
            String carrera = jsonObject.get("carrera").getAsString();
            int edad = jsonObject.get("edad").getAsInt();

            Usuario nuevoUsuario = new Usuario(nombre, correo, contraseña, carrera, edad);
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            boolean registrado = usuarioDAO.registrarUsuario(nuevoUsuario);

            String response = registrado ? "Usuario registrado con éxito" : "Error al registrar usuario";
            exchange.sendResponseHeaders(registrado ? 200 : 400, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
        }
    }
}

class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Verificar que el método sea POST
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            // Extraer correo y contraseña del JSON
            String correo = jsonObject.get("correo").getAsString();
            String contraseña = jsonObject.get("contraseña").getAsString();

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.obtenerUsuarioPorCorreo(correo);

            String response;
            if (usuario != null && usuario.getContraseña().equals(contraseña)) {
                // Respuesta exitosa con datos del usuario
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Inicio de sesión exitoso");
                responseJson.addProperty("nombre", usuario.getNombre());
                responseJson.addProperty("correo", usuario.getCorreo());

                response = responseJson.toString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
                // Respuesta en caso de credenciales incorrectas
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "Credenciales incorrectas");

                response = responseJson.toString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }

            // Enviar la respuesta al cliente
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            // Si no es POST, responde con 405 Method Not Allowed
            exchange.sendResponseHeaders(405, -1);
        }
    }
}


