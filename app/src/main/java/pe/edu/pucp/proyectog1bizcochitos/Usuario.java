package pe.edu.pucp.proyectog1bizcochitos;

public class Usuario {

    private String nombre, correo, codigo;

    public Usuario() {

    }

    public Usuario(String nombre, String correo, String codigo) {
        this.nombre = nombre;
        this.correo = correo;
        this.codigo = codigo;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
