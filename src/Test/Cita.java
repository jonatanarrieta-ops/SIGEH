package Test;

/**
 * Modelo de datos: Cita médica
 */
public class Cita {

    public enum Estado { PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA }

    private int    id;
    private String paciente;
    private String medico;
    private String especialidad;
    private String fecha;
    private String hora;
    private Estado estado;
    private String observaciones;

    public Cita(int id, String paciente, String medico, String especialidad,
                String fecha, String hora, Estado estado, String observaciones) {
        this.id            = id;
        this.paciente      = paciente;
        this.medico        = medico;
        this.especialidad  = especialidad;
        this.fecha         = fecha;
        this.hora          = hora;
        this.estado        = estado;
        this.observaciones = observaciones;
    }

    public int    getId()            { return id; }
    public String getPaciente()      { return paciente; }
    public String getMedico()        { return medico; }
    public String getEspecialidad()  { return especialidad; }
    public String getFecha()         { return fecha; }
    public String getHora()          { return hora; }
    public Estado getEstado()        { return estado; }
    public String getObservaciones() { return observaciones; }

    public void setEstado(Estado e)        { this.estado = e; }
    public void setObservaciones(String o) { this.observaciones = o; }
    public void setFecha(String f)         { this.fecha = f; }
    public void setHora(String h)          { this.hora = h; }
}
