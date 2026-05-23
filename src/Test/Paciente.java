package Test;

/**
 * Modelo de datos: Paciente
 */
public class Paciente {

    private int    id;
    private String nombre;
    private String cedula;
    private String fechaNacimiento;
    private String genero;
    private String telefono;
    private String correo;
    private String tipoSangre;
    private String eps;
    private String direccion;

    public Paciente(int id, String nombre, String cedula, String fechaNacimiento,
                    String genero, String telefono, String correo,
                    String tipoSangre, String eps, String direccion) {
        this.id              = id;
        this.nombre          = nombre;
        this.cedula          = cedula;
        this.fechaNacimiento = fechaNacimiento;
        this.genero          = genero;
        this.telefono        = telefono;
        this.correo          = correo;
        this.tipoSangre      = tipoSangre;
        this.eps             = eps;
        this.direccion       = direccion;
    }

    public int    getId()              { return id; }
    public String getNombre()          { return nombre; }
    public String getCedula()          { return cedula; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero()          { return genero; }
    public String getTelefono()        { return telefono; }
    public String getCorreo()          { return correo; }
    public String getTipoSangre()      { return tipoSangre; }
    public String getEps()             { return eps; }
    public String getDireccion()       { return direccion; }

    public void setNombre(String n)          { this.nombre = n; }
    public void setCedula(String c)          { this.cedula = c; }
    public void setFechaNacimiento(String f) { this.fechaNacimiento = f; }
    public void setGenero(String g)          { this.genero = g; }
    public void setTelefono(String t)        { this.telefono = t; }
    public void setCorreo(String c)          { this.correo = c; }
    public void setTipoSangre(String ts)     { this.tipoSangre = ts; }
    public void setEps(String e)             { this.eps = e; }
    public void setDireccion(String d)       { this.direccion = d; }

    @Override
    public String toString() { return nombre + " (" + cedula + ")"; }
}
