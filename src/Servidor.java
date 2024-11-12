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
import java.io.*;
import database.EventoDAO;
import models.Evento;

public class Servidor {

	public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/usuarios/registro", new RegistroHandler());
        server.createContext("/api/usuarios/login", new LoginHandler());
        server.createContext("/api/usuarios", new UsuariosHandler());
        server.createContext("/api/eventos", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                EventoDAO eventoDAO = new EventoDAO();
                Gson gson = new Gson();

                try {
                    if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                     
                        List<Evento> eventos = eventoDAO.obtenerEventos();
                        String jsonResponse = gson.toJson(eventos);

                        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");

                        byte[] responseBytes = jsonResponse.getBytes("UTF-8");
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(responseBytes);
                        }

                    } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                      
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
                            Evento nuevoEvento = gson.fromJson(reader, Evento.class);
                            boolean creado = eventoDAO.crearEvento(nuevoEvento);

                            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
                            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                            if (creado) {
                                exchange.sendResponseHeaders(201, -1); 
                            } else {
                                exchange.sendResponseHeaders(500, -1); 
                            }
                        }

                    } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                        
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
                            Evento eventoActualizado = gson.fromJson(reader, Evento.class);

                        
                            if (eventoDAO.verificarPermiso(eventoActualizado.getId(), eventoActualizado.getUsuarioId())) {
                                eventoDAO.actualizarEvento(eventoActualizado);
                                exchange.sendResponseHeaders(200, -1);
                            } else {
                                exchange.sendResponseHeaders(403, -1); 
                            }
                        }

                    } else if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
                    
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
                            Evento eventoAEliminar = gson.fromJson(reader, Evento.class);
                            if (eventoDAO.verificarPermiso(eventoAEliminar.getId(), eventoAEliminar.getUsuarioId())) {
                                eventoDAO.eliminarEvento(eventoAEliminar.getId());
                                exchange.sendResponseHeaders(200, -1); 
                            } else {
                                exchange.sendResponseHeaders(403, -1);
                            }
                        }

                    } else {
                        exchange.sendResponseHeaders(405, -1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                }
            }
        });
        
        server.createContext("/", new StaticFileHandler());

 
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor iniciado en el puerto 8080");
    }
}
class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();
        String filePath;

        
        if (uriPath.startsWith("/static/")) {
            filePath = "static" + uriPath.substring("/static".length());
        } else {
            filePath = "templates" + uriPath;
            if (filePath.equals("templates/") || filePath.endsWith("/")) {
                filePath += "index.html";
            }
        }

        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            if (filePath.endsWith(".html")) {
                exchange.getResponseHeaders().set("Content-Type", "text/html");
            } else if (filePath.endsWith(".css")) {
                exchange.getResponseHeaders().set("Content-Type", "text/css");
            } else if (filePath.endsWith(".js")) {
                exchange.getResponseHeaders().set("Content-Type", "application/javascript");
            }

            byte[] fileData = new FileInputStream(file).readAllBytes();
            exchange.sendResponseHeaders(200, fileData.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileData);
            os.close();
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }
}


class UsuariosHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Usuario> usuarios = new ArrayList<>();
            Gson gson = new Gson();
            OutputStream os = exchange.getResponseBody(); 

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT nombre, correo, carrera, edad FROM usuarios";
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    Usuario usuario = new Usuario(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        "",                   
                        rs.getString("carrera"),
                        rs.getInt("edad")
                    );
                    usuarios.add(usuario);
                }

                String jsonResponse = gson.toJson(usuarios);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, 0); 

                os.write(jsonResponse.getBytes());

            } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(500, 0); 
                os.write("Error del servidor".getBytes());
            } finally {
                os.close(); 
            }
        } else {
            exchange.sendResponseHeaders(405, -1); 
        }
    }
}



class RegistroHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

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
            exchange.sendResponseHeaders(405, -1); 
        }
    }
}


class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            String correo = jsonObject.get("correo").getAsString();
            String contraseña = jsonObject.get("contraseña").getAsString();

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.obtenerUsuarioPorCorreo(correo);

            String response;
            if (usuario != null && usuario.getContraseña().equals(contraseña)) {
                
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Inicio de sesión exitoso");
                responseJson.addProperty("id", usuario.getId());
                responseJson.addProperty("nombre", usuario.getNombre());
                responseJson.addProperty("correo", usuario.getCorreo());
                responseJson.addProperty("carrera", usuario.getCarrera());
                responseJson.addProperty("edad", usuario.getEdad());

                response = responseJson.toString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
               
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "Credenciales incorrectas");

                response = responseJson.toString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }

            
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}




