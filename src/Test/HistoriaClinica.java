package Test;

/**
 * Modelo de datos: Historia Clínica
 */
public class HistoriaClinica {

    private int    id;
    private String paciente;
    private String medico;
    private String fecha;
    private String motivo;
    private String diagnostico;
    private String tratamiento;
    private String medicamentos;
    private String proximaCita;

    public HistoriaClinica(int id, String paciente, String medico, String fecha,
                           String motivo, String diagnostico, String tratamiento,
                           String medicamentos, String proximaCita) {
        this.id           = id;
        this.paciente     = paciente;
        this.medico       = medico;
        this.fecha        = fecha;
        this.motivo       = motivo;
        this.diagnostico  = diagnostico;
        this.tratamiento  = tratamiento;
        this.medicamentos = medicamentos;
        this.proximaCita  = proximaCita;
    }

    public int    getId()           { return id; }
    public String getPaciente()     { return paciente; }
    public String getMedico()       { return medico; }
    public String getFecha()        { return fecha; }
    public String getMotivo()       { return motivo; }
    public String getDiagnostico()  { return diagnostico; }
    public String getTratamiento()  { return tratamiento; }
    public String getMedicamentos() { return medicamentos; }
    public String getProximaCita()  { return proximaCita; }
}
