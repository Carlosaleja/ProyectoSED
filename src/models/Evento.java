package models;
import org.owasp.encoder.Encode;

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
        this.titulo = Encode.forHtml(titulo); 
        this.descripcion = Encode.forHtml(descripcion);
        this.categoria = Encode.forHtml(categoria); 
        this.fecha = Encode.forHtml(fecha); 
        this.importancia = Encode.forHtml(importancia); 
        this.usuarioId = usuarioId; 
    }


    public Evento(String titulo, String descripcion, String categoria, String fecha, String importancia, int usuarioId) {
        this.titulo = Encode.forHtml(titulo);
        this.descripcion = Encode.forHtml(descripcion);
        this.categoria = Encode.forHtml(categoria);
        this.fecha = Encode.forHtml(fecha);
        this.importancia = Encode.forHtml(importancia);
        this.usuarioId = usuarioId;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

	public void setTitulo(String titulo) {
        this.titulo = Encode.forHtml(titulo);
    }

    public String getDescripcion() {
        return descripcion;
    }

	public void setDescripcion(String descripcion) {
        this.descripcion = Encode.forHtml(descripcion); 
    }

    public String getCategoria() {
        return categoria;
    }

	 public void setCategoria(String categoria) {
        this.categoria = Encode.forHtml(categoria);
    }

    public String getFecha() {
        return fecha;
    }

	public void setFecha(String fecha) {
        this.fecha = Encode.forHtml(fecha);
    }

    public String getImportancia() {
        return importancia;
    }

	public void setImportancia(String importancia) {
    this.importancia = importancia;
}


    public int getUsuarioId() {
        return usuarioId;
    }

	public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

	@Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", fecha='" + fecha + '\'' +
                ", importancia='" + importancia + '\'' +
                ", usuarioId=" + usuarioId +
                '}';
    }

}
