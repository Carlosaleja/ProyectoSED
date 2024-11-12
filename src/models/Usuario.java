package models;

public class Usuario {
    private int id;  // Cambiado de 'id' a 'usuarioId'
    private String nombre;
    private String correo;
    private String contraseña;
    private String carrera;
    private int edad;

    // Constructor
    public Usuario(String nombre, String correo, String contraseña, String carrera, int edad) {
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.carrera = carrera;
        this.edad = edad;
    }

    // Getters y Setters
    public int getId() {  // Cambiado de 'getId' a 'getUsuarioId'
        return id;
    }

    public void setId(int id) {  // Cambiado de 'setId' a 'setUsuarioId'
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}

