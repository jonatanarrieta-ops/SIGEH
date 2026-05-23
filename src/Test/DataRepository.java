package Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria — simula la capa de persistencia.
 * Singleton compartido por todos los módulos de la aplicación.
 */
public class DataRepository {

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static DataRepository instancia;
    public  static DataRepository getInstance() {
        if (instancia == null) instancia = new DataRepository();
        return instancia;
    }

    // ── Contadores de ID ──────────────────────────────────────────────────────
    private int contadorPacientes  = 1;
    private int contadorCitas      = 1;
    private int contadorFacturas   = 1;
    private int contadorHistorias  = 1;

    // ── Colecciones ───────────────────────────────────────────────────────────
    private final List<Paciente>       pacientes  = new ArrayList<>();
    private final List<Cita>           citas      = new ArrayList<>();
    private final List<Factura>        facturas   = new ArrayList<>();
    private final List<HistoriaClinica> historias = new ArrayList<>();

    // ── Constructor privado con datos de muestra ──────────────────────────────
    private DataRepository() {
        cargarDatosDemostracion();
    }

    private void cargarDatosDemostracion() {
        // Pacientes de ejemplo
        agregarPaciente(new Paciente(contadorPacientes++, "María González",   "1023456789", "1990-05-14", "Femenino",   "3001234567", "maria@email.com",  "O+",  "Sura",     "Calle 10 #5-23"));
        agregarPaciente(new Paciente(contadorPacientes++, "Carlos Rodríguez", "1045678901", "1985-11-03", "Masculino",  "3109876543", "carlos@email.com", "A+",  "Sanitas",  "Carrera 7 #12-45"));
        agregarPaciente(new Paciente(contadorPacientes++, "Ana Martínez",     "1067890123", "2000-03-22", "Femenino",   "3204567890", "ana@email.com",    "B-",  "Compensar","Transversal 18 #30-10"));
        agregarPaciente(new Paciente(contadorPacientes++, "Luis Pérez",       "1089012345", "1978-08-30", "Masculino",  "3156789012", "luis@email.com",   "AB+", "Famisanar","Avenida 1 #25-60"));

        // Citas de ejemplo
        agregarCita(new Cita(contadorCitas++, "María González",   "Dr. Ramírez",  "Medicina General", "2026-04-28", "08:00", Cita.Estado.CONFIRMADA,  "Control de rutina"));
        agregarCita(new Cita(contadorCitas++, "Carlos Rodríguez", "Dra. Torres",  "Cardiología",      "2026-04-29", "10:30", Cita.Estado.PENDIENTE,   "Revisión ecocardiograma"));
        agregarCita(new Cita(contadorCitas++, "Ana Martínez",     "Dr. López",    "Pediatría",        "2026-04-30", "14:00", Cita.Estado.CONFIRMADA,  "Vacunación anual"));
        agregarCita(new Cita(contadorCitas++, "Luis Pérez",       "Dra. Suárez",  "Ortopedia",        "2026-05-02", "09:00", Cita.Estado.PENDIENTE,   "Radiografía rodilla"));

        // Facturas de ejemplo
        agregarFactura(new Factura(contadorFacturas++, "María González",   "Consulta General",    80000,  0,    0,   "2026-04-20", Factura.Estado.PAGADA,    "Efectivo"));
        agregarFactura(new Factura(contadorFacturas++, "Carlos Rodríguez", "Ecocardiograma",     350000, 5000, 19,  "2026-04-21", Factura.Estado.PENDIENTE, "Tarjeta"));
        agregarFactura(new Factura(contadorFacturas++, "Ana Martínez",     "Vacunación Completa",120000, 0,    0,   "2026-04-22", Factura.Estado.PAGADA,    "Transferencia"));

        // Historias clínicas de ejemplo
        agregarHistoria(new HistoriaClinica(contadorHistorias++, "María González",   "Dr. Ramírez", "2026-04-01",
                "Dolor de cabeza persistente", "Migraña tensional", "Reposo y analgésicos",
                "Ibuprofeno 400mg c/8h", "2026-05-01"));
        agregarHistoria(new HistoriaClinica(contadorHistorias++, "Carlos Rodríguez", "Dra. Torres", "2026-03-15",
                "Fatiga y palpitaciones", "Arritmia supraventricular", "Monitoreo Holter",
                "Metoprolol 25mg c/12h", "2026-04-29"));
    }

    // ── CRUD Pacientes ────────────────────────────────────────────────────────
    public void          agregarPaciente(Paciente p)  { pacientes.add(p); }
    public List<Paciente> getPacientes()              { return new ArrayList<>(pacientes); }
    public Paciente       getPacientePorId(int id)    { return pacientes.stream().filter(p -> p.getId() == id).findFirst().orElse(null); }
    public boolean        eliminarPaciente(int id)    { return pacientes.removeIf(p -> p.getId() == id); }
    public int            nuevoIdPaciente()           { return contadorPacientes++; }

    public List<Paciente> buscarPacientes(String texto) {
        String t = texto.toLowerCase();
        return pacientes.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(t) || p.getCedula().contains(t))
                .collect(Collectors.toList());
    }

    // ── CRUD Citas ────────────────────────────────────────────────────────────
    public void       agregarCita(Cita c)   { citas.add(c); }
    public List<Cita> getCitas()            { return new ArrayList<>(citas); }
    public boolean    eliminarCita(int id)  { return citas.removeIf(c -> c.getId() == id); }
    public int        nuevoIdCita()         { return contadorCitas++; }

    // ── CRUD Facturas ─────────────────────────────────────────────────────────
    public void          agregarFactura(Factura f) { facturas.add(f); }
    public List<Factura> getFacturas()             { return new ArrayList<>(facturas); }
    public boolean       eliminarFactura(int id)   { return facturas.removeIf(f -> f.getId() == id); }
    public int           nuevoIdFactura()          { return contadorFacturas++; }

    // ── CRUD Historias ────────────────────────────────────────────────────────
    public void                agregarHistoria(HistoriaClinica h)  { historias.add(h); }
    public List<HistoriaClinica> getHistorias()                    { return new ArrayList<>(historias); }
    public int                 nuevoIdHistoria()                   { return contadorHistorias++; }

    public List<HistoriaClinica> getHistoriasPorPaciente(String nombre) {
        return historias.stream()
                .filter(h -> h.getPaciente().equalsIgnoreCase(nombre))
                .collect(Collectors.toList());
    }

    // ── Estadísticas para el Dashboard ────────────────────────────────────────
    public int totalPacientes()          { return pacientes.size(); }
    public int citasPendientes()         { return (int) citas.stream().filter(c -> c.getEstado() == Cita.Estado.PENDIENTE || c.getEstado() == Cita.Estado.CONFIRMADA).count(); }
    public int facturasPendientes()      { return (int) facturas.stream().filter(f -> f.getEstado() == Factura.Estado.PENDIENTE).count(); }
    public double ingresosTotales()      { return facturas.stream().filter(f -> f.getEstado() == Factura.Estado.PAGADA).mapToDouble(Factura::getTotal).sum(); }
}
