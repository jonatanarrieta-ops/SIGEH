package Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Módulo de Historia Clínica — registro y consulta por paciente.
 */
public class HistoriaClinicaPanel extends JPanel {

    private final DataRepository repo = DataRepository.getInstance();
    private DefaultTableModel modelo;
    private JTable tabla;

    private static final String[] MEDICOS = {
        "Dr. Ramírez", "Dra. Torres", "Dr. López", "Dra. Suárez",
        "Dr. García",  "Dra. Herrera","Dr. Castro", "Dra. Moreno"
    };

    public HistoriaClinicaPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.GRIS_FONDO);
        construir();
    }

    private void construir() {
        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirCuerpo(),     BorderLayout.CENTER);
        cargarTabla(repo.getHistorias());
    }

    private JPanel construirEncabezado() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Color.WHITE);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.GRIS_BORDE),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(UIUtils.label("Historia Clínica", UIUtils.F_TITULO, UIUtils.AZUL_OSCURO));
        textos.add(UIUtils.label("Registro médico de consultas y tratamientos por paciente", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));

        JButton btnNueva = UIUtils.botonPrimario("+ Nueva Historia");
        btnNueva.addActionListener(e -> mostrarFormulario());

        h.add(textos, BorderLayout.CENTER);
        h.add(btnNueva, BorderLayout.EAST);
        return h;
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // Búsqueda por paciente
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        JTextField campoBusq = UIUtils.barraBusqueda("Buscar por nombre de paciente...");
        campoBusq.setPreferredSize(new Dimension(300, 36));
        JButton btnBuscar = UIUtils.botonPrimario("Buscar");
        JButton btnTodos  = UIUtils.botonSecundario("Ver todas");
        btnBuscar.addActionListener(e -> {
            String texto = campoBusq.getText().trim();
            if (!texto.isEmpty() && !texto.equals("Buscar por nombre de paciente...")) {
                cargarTabla(repo.getHistoriasPorPaciente(texto));
            }
        });
        btnTodos.addActionListener(e -> { campoBusq.setText(""); cargarTabla(repo.getHistorias()); });
        barra.add(campoBusq);
        barra.add(btnBuscar);
        barra.add(btnTodos);

        // Tabla
        String[] cols = {"ID", "Paciente", "Médico", "Fecha", "Motivo", "Diagnóstico"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = UIUtils.tabla(modelo);
        tabla.getColumnModel().getColumn(0).setMaxWidth(45);

        // Doble clic para ver detalle completo
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) verDetalle();
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        JButton btnVer = UIUtils.botonSecundario("Ver registro completo", new MenuIcon(MenuIcon.Tipo.VER, 14, new Color(33,33,33)));
        btnVer.addActionListener(e -> verDetalle());
        botones.add(btnVer);

        cuerpo.add(barra,                      BorderLayout.NORTH);
        cuerpo.add(UIUtils.scrollTabla(tabla), BorderLayout.CENTER);
        cuerpo.add(botones,                    BorderLayout.SOUTH);
        return cuerpo;
    }

    private void mostrarFormulario() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Historia Clínica", true);
        dialog.setSize(580, 640);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(UIUtils.AZUL_OSCURO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        header.add(UIUtils.label("Registrar nueva historia clínica", UIUtils.F_SUBTITULO, Color.WHITE));
        dialog.add(header, BorderLayout.NORTH);

        List<Paciente> pacientes = repo.getPacientes();
        String[] nombres = pacientes.stream().map(Paciente::getNombre).toArray(String[]::new);

        JComboBox<String> comboPaciente = UIUtils.combo(nombres);
        JComboBox<String> comboMedico   = UIUtils.combo(MEDICOS);
        JTextField campoFecha           = UIUtils.campo();
        JTextArea  areaMotivo           = UIUtils.area(3, 30);
        JTextArea  areaDiag             = UIUtils.area(3, 30);
        JTextArea  areaTrat             = UIUtils.area(3, 30);
        JTextArea  areaMeds             = UIUtils.area(2, 30);
        JTextField campoProxima         = UIUtils.campo();

        campoFecha.setText(java.time.LocalDate.now().toString());

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        agregarCampo(form, "Paciente *",      comboPaciente);
        agregarCampo(form, "Médico *",        comboMedico);
        agregarCampo(form, "Fecha",           campoFecha);
        agregarAreaTexto(form, "Motivo de consulta *", areaMotivo);
        agregarAreaTexto(form, "Diagnóstico *",         areaDiag);
        agregarAreaTexto(form, "Tratamiento",            areaTrat);
        agregarAreaTexto(form, "Medicamentos",           areaMeds);
        agregarCampo(form, "Próxima cita",    campoProxima);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        pie.setBackground(UIUtils.GRIS_FONDO);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.GRIS_BORDE));
        JButton btnCancelar = UIUtils.botonSecundario("Cancelar");
        JButton btnGuardar  = UIUtils.botonExito("Guardar historia");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            String paciente = (String) comboPaciente.getSelectedItem();
            String motivo   = areaMotivo.getText().trim();
            String diag     = areaDiag.getText().trim();
            if (paciente == null || motivo.isEmpty() || diag.isEmpty()) {
                UIUtils.alerta(dialog, "Paciente, motivo y diagnóstico son obligatorios."); return;
            }
            repo.agregarHistoria(new HistoriaClinica(
                repo.nuevoIdHistoria(), paciente,
                (String) comboMedico.getSelectedItem(),
                campoFecha.getText().trim(), motivo, diag,
                areaTrat.getText().trim(),
                areaMeds.getText().trim(),
                campoProxima.getText().trim()));
            cargarTabla(repo.getHistorias());
            UIUtils.exito(this, "Historia clínica registrada.");
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
        campo.setPreferredSize(new Dimension(340, 34));
        fila.add(lbl,  BorderLayout.WEST);
        fila.add(campo,BorderLayout.CENTER);
        form.add(fila);
    }

    private void agregarAreaTexto(JPanel form, String etiqueta, JTextArea area) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        fila.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel lbl = UIUtils.label(etiqueta, UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO);
        lbl.setPreferredSize(new Dimension(170, 30));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(340, 72));
        fila.add(lbl,   BorderLayout.WEST);
        fila.add(scroll,BorderLayout.CENTER);
        form.add(fila);
    }

    private void cargarTabla(List<HistoriaClinica> lista) {
        modelo.setRowCount(0);
        for (HistoriaClinica h : lista) {
            modelo.addRow(new Object[]{
                h.getId(), h.getPaciente(), h.getMedico(),
                h.getFecha(), h.getMotivo(), h.getDiagnostico()
            });
        }
    }

    private void verDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { UIUtils.alerta(this, "Seleccione una historia clínica."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        HistoriaClinica h = repo.getHistorias().stream()
                .filter(x -> x.getId() == id).findFirst().orElse(null);
        if (h == null) return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Historia Clínica — " + h.getPaciente(), true);
        d.setSize(520, 500);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        String[][] campos = {
            {"Paciente",       h.getPaciente()},
            {"Médico",         h.getMedico()},
            {"Fecha",          h.getFecha()},
            {"Motivo",         h.getMotivo()},
            {"Diagnóstico",    h.getDiagnostico()},
            {"Tratamiento",    h.getTratamiento()},
            {"Medicamentos",   h.getMedicamentos()},
            {"Próxima cita",   h.getProximaCita()}
        };
        for (String[] c : campos) {
            JPanel bloque = new JPanel(new BorderLayout(0, 2));
            bloque.setOpaque(false);
            bloque.add(UIUtils.label(c[0].toUpperCase(), UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS), BorderLayout.NORTH);
            JTextArea ta = new JTextArea(c[1]);
            ta.setFont(UIUtils.F_NORMAL);
            ta.setEditable(false);
            ta.setBackground(UIUtils.GRIS_FONDO);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            bloque.add(ta, BorderLayout.CENTER);
            panel.add(bloque);
        }

        JButton cerrar = UIUtils.botonPrimario("Cerrar");
        cerrar.addActionListener(e -> d.dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pie.setBackground(Color.WHITE);
        pie.add(cerrar);

        d.add(new JScrollPane(panel), BorderLayout.CENTER);
        d.add(pie, BorderLayout.SOUTH);
        d.setVisible(true);
    }
}