package Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Módulo de Gestión de Citas — CRUD completo con cambio de estado.
 */
public class CitasPanel extends JPanel {

    private final DataRepository repo = DataRepository.getInstance();

    private DefaultTableModel modelo;
    private JTable tabla;

    private static final String[] MEDICOS = {
        "Dr. Ramírez", "Dra. Torres", "Dr. López", "Dra. Suárez",
        "Dr. García",  "Dra. Herrera","Dr. Castro", "Dra. Moreno"
    };
    private static final String[] ESPECIALIDADES = {
        "Medicina General", "Cardiología", "Pediatría", "Ortopedia",
        "Dermatología", "Neurología", "Ginecología", "Oftalmología"
    };

    public CitasPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.GRIS_FONDO);
        construir();
    }

    private void construir() {
        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirCuerpo(),     BorderLayout.CENTER);
        cargarTabla();
    }

    private JPanel construirEncabezado() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Color.WHITE);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.GRIS_BORDE),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        JLabel titulo = UIUtils.label("Gestión de Citas", UIUtils.F_TITULO, UIUtils.AZUL_OSCURO);
        JLabel sub    = UIUtils.label("Programación y seguimiento de citas médicas", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS);
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(sub);

        JButton btnNueva = UIUtils.botonPrimario("+ Nueva Cita");
        btnNueva.addActionListener(e -> mostrarFormulario(null));

        h.add(textos, BorderLayout.CENTER);
        h.add(btnNueva, BorderLayout.EAST);
        return h;
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // Filtros rápidos de estado
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtros.setOpaque(false);
        filtros.add(UIUtils.label("Filtrar: ", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));
        for (String estado : new String[]{"Todas", "PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA"}) {
            JButton btn = UIUtils.botonSecundario(estado);
            btn.addActionListener(e -> filtrarPorEstado(estado));
            filtros.add(btn);
        }

        // Tabla
        String[] columnas = {"ID", "Paciente", "Médico", "Especialidad", "Fecha", "Hora", "Estado"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = UIUtils.tabla(modelo);
        tabla.getColumnModel().getColumn(0).setMaxWidth(45);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Botones de acción
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        JButton btnEditar     = UIUtils.botonPrimario("Editar", new MenuIcon(MenuIcon.Tipo.EDITAR, 14, Color.WHITE));
        JButton btnConfirmar  = UIUtils.botonExito("Confirmar", new MenuIcon(MenuIcon.Tipo.CONFIRMAR, 14, Color.WHITE));
        JButton btnCancelar   = UIUtils.botonPeligro("Cancelar cita", new MenuIcon(MenuIcon.Tipo.CANCELAR, 14, Color.WHITE));
        JButton btnCompletar  = UIUtils.botonSecundario("Completar", new MenuIcon(MenuIcon.Tipo.COMPLETAR, 14, new Color(33,33,33)));
        JButton btnEliminar   = UIUtils.botonPeligro("Eliminar", new MenuIcon(MenuIcon.Tipo.ELIMINAR, 14, Color.WHITE));

        btnEditar.addActionListener(e    -> editarSeleccionada());
        btnConfirmar.addActionListener(e -> cambiarEstado(Cita.Estado.CONFIRMADA));
        btnCancelar.addActionListener(e  -> cambiarEstado(Cita.Estado.CANCELADA));
        btnCompletar.addActionListener(e -> cambiarEstado(Cita.Estado.COMPLETADA));
        btnEliminar.addActionListener(e  -> eliminarSeleccionada());

        botones.add(btnEditar);
        botones.add(btnConfirmar);
        botones.add(btnCompletar);
        botones.add(btnCancelar);
        botones.add(btnEliminar);

        cuerpo.add(filtros,                    BorderLayout.NORTH);
        cuerpo.add(UIUtils.scrollTabla(tabla), BorderLayout.CENTER);
        cuerpo.add(botones,                    BorderLayout.SOUTH);
        return cuerpo;
    }

    // ── FORMULARIO ─────────────────────────────────────────────────────────────
    private void mostrarFormulario(Cita cita) {
        boolean esEdicion = cita != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Editar Cita" : "Nueva Cita", true);
        dialog.setSize(500, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel tituloPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBackground(UIUtils.AZUL_OSCURO);
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        tituloPanel.add(UIUtils.label(esEdicion ? "Editar cita" : "Registrar nueva cita",
                UIUtils.F_SUBTITULO, Color.WHITE));
        dialog.add(tituloPanel, BorderLayout.NORTH);

        // Obtener lista de pacientes para el combo
        List<Paciente> pacientes = repo.getPacientes();
        String[] nombresPacientes = pacientes.stream().map(Paciente::getNombre).toArray(String[]::new);

        JComboBox<String> comboPaciente      = UIUtils.combo(nombresPacientes);
        JComboBox<String> comboMedico        = UIUtils.combo(MEDICOS);
        JComboBox<String> comboEspecialidad  = UIUtils.combo(ESPECIALIDADES);
        JTextField        campoFecha         = UIUtils.campo();
        JTextField        campoHora          = UIUtils.campo();
        JTextArea         areaObs            = UIUtils.area(3, 30);
        JComboBox<String> comboEstado        = UIUtils.combo("PENDIENTE","CONFIRMADA","CANCELADA","COMPLETADA");

        campoFecha.setToolTipText("Formato: YYYY-MM-DD");
        campoHora.setToolTipText("Formato: HH:MM");

        if (esEdicion) {
            comboPaciente.setSelectedItem(cita.getPaciente());
            comboMedico.setSelectedItem(cita.getMedico());
            comboEspecialidad.setSelectedItem(cita.getEspecialidad());
            campoFecha.setText(cita.getFecha());
            campoHora.setText(cita.getHora());
            areaObs.setText(cita.getObservaciones());
            comboEstado.setSelectedItem(cita.getEstado().toString());
        }

        // Actualiza especialidades al cambiar médico
        comboMedico.addActionListener(e -> {
            int idx = comboMedico.getSelectedIndex();
            if (idx >= 0 && idx < ESPECIALIDADES.length) comboEspecialidad.setSelectedIndex(idx);
        });

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        agregarCampo(form, "Paciente *",      comboPaciente);
        agregarCampo(form, "Médico *",        comboMedico);
        agregarCampo(form, "Especialidad",    comboEspecialidad);
        agregarCampo(form, "Fecha * (YYYY-MM-DD)", campoFecha);
        agregarCampo(form, "Hora * (HH:MM)",  campoHora);
        agregarCampo(form, "Estado",          comboEstado);

        JPanel filaNota = new JPanel(new BorderLayout(12, 0));
        filaNota.setOpaque(false);
        filaNota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        filaNota.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel lblNota = UIUtils.label("Observaciones", UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO);
        lblNota.setPreferredSize(new Dimension(170, 30));
        JScrollPane scrollObs = new JScrollPane(areaObs);
        scrollObs.setPreferredSize(new Dimension(280, 70));
        filaNota.add(lblNota,    BorderLayout.WEST);
        filaNota.add(scrollObs, BorderLayout.CENTER);
        form.add(filaNota);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);

        // Botones
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        pie.setBackground(UIUtils.GRIS_FONDO);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.GRIS_BORDE));
        JButton btnCancelar = UIUtils.botonSecundario("Cancelar");
        JButton btnGuardar  = UIUtils.botonExito(esEdicion ? "Guardar cambios" : "Registrar cita");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            String paciente = (String) comboPaciente.getSelectedItem();
            String medico   = (String) comboMedico.getSelectedItem();
            String fecha    = campoFecha.getText().trim();
            String hora     = campoHora.getText().trim();
            if (paciente == null || medico == null || fecha.isEmpty() || hora.isEmpty()) {
                UIUtils.alerta(dialog, "Paciente, médico, fecha y hora son obligatorios."); return;
            }
            Cita.Estado estado = Cita.Estado.valueOf((String) comboEstado.getSelectedItem());
            if (esEdicion) {
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setEstado(estado);
                cita.setObservaciones(areaObs.getText().trim());
            } else {
                repo.agregarCita(new Cita(repo.nuevoIdCita(), paciente, medico,
                        (String) comboEspecialidad.getSelectedItem(),
                        fecha, hora, estado, areaObs.getText().trim()));
            }
            cargarTabla();
            UIUtils.exito(this, "Cita guardada correctamente.");
            dialog.dispose();
        });
        pie.add(btnCancelar);
        pie.add(btnGuardar);
        dialog.add(pie, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void agregarCampo(JPanel form, String etiqueta, JComponent campo) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        fila.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel lbl = UIUtils.label(etiqueta, UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO);
        lbl.setPreferredSize(new Dimension(170, 30));
        campo.setPreferredSize(new Dimension(280, 34));
        fila.add(lbl, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        form.add(fila);
    }

    // ── OPERACIONES ─────────────────────────────────────────────────────────────
    private void cargarTabla() {
        cargarTabla(repo.getCitas());
    }

    private void cargarTabla(List<Cita> lista) {
        modelo.setRowCount(0);
        for (Cita c : lista) {
            modelo.addRow(new Object[]{
                c.getId(), c.getPaciente(), c.getMedico(),
                c.getEspecialidad(), c.getFecha(), c.getHora(),
                c.getEstado().toString()
            });
        }
    }

    private Cita getCitaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { UIUtils.alerta(this, "Seleccione una cita."); return null; }
        int id = (int) modelo.getValueAt(fila, 0);
        return repo.getCitas().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    private void editarSeleccionada() {
        Cita c = getCitaSeleccionada();
        if (c != null) mostrarFormulario(c);
    }

    private void cambiarEstado(Cita.Estado nuevoEstado) {
        Cita c = getCitaSeleccionada();
        if (c == null) return;
        c.setEstado(nuevoEstado);
        cargarTabla();
        UIUtils.exito(this, "Estado actualizado a: " + nuevoEstado);
    }

    private void eliminarSeleccionada() {
        Cita c = getCitaSeleccionada();
        if (c == null) return;
        if (UIUtils.confirmar(this, "¿Eliminar la cita de " + c.getPaciente() + "?")) {
            repo.eliminarCita(c.getId());
            cargarTabla();
        }
    }

    private void filtrarPorEstado(String estado) {
        if (estado.equals("Todas")) { cargarTabla(); return; }
        List<Cita> filtradas = repo.getCitas().stream()
                .filter(c -> c.getEstado().toString().equals(estado))
                .collect(java.util.stream.Collectors.toList());
        cargarTabla(filtradas);
    }
}