package models;

public class Evento {
    private int id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String fecha;
    private String importancia;
    private int usuarioId;

    public Evento(int id, String titulo, String descripcion, String categoria, String fecha, String importancia, int usuarioId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fecha = fecha;
        this.importancia = importancia;
        this.usuarioId = usuarioId;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFecha() {
        return fecha;
    }

    public String getImportancia() {
        return importancia;
    }

    public int getUsuarioId() {
        return usuarioId;
    }
}
