package Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Módulo de Gestión de Pacientes — CRUD completo con búsqueda y detalle.
 */
public class PacientesPanel extends JPanel {

    private final DataRepository repo = DataRepository.getInstance();

    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField campoBusqueda;

    // Campos del formulario
    private JTextField campoNombre, campoCedula, campoFecha, campoTelefono,
                       campoCorreo, campoDireccion;
    private JComboBox<String> comboGenero, comboSangre, comboEps;

    public PacientesPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIUtils.GRIS_FONDO);
        construir();
    }

    private void construir() {
        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirCuerpo(),     BorderLayout.CENTER);
        cargarTabla(repo.getPacientes());
    }

    // ── ENCABEZADO ─────────────────────────────────────────────────────────────
    private JPanel construirEncabezado() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Color.WHITE);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.GRIS_BORDE),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        JLabel titulo = UIUtils.label("Gestión de Pacientes", UIUtils.F_TITULO, UIUtils.AZUL_OSCURO);
        JLabel sub    = UIUtils.label("Registro, consulta y administración de pacientes", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS);
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(sub);

        JButton btnNuevo = UIUtils.botonPrimario("+ Nuevo Paciente");
        btnNuevo.addActionListener(e -> mostrarFormulario(null));

        h.add(textos, BorderLayout.CENTER);
        h.add(btnNuevo, BorderLayout.EAST);
        return h;
    }

    // ── CUERPO PRINCIPAL ───────────────────────────────────────────────────────
    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // Barra de búsqueda
        JPanel barraBusq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBusq.setOpaque(false);
        campoBusqueda = UIUtils.barraBusqueda("Buscar por nombre o cédula...");
        campoBusqueda.setPreferredSize(new Dimension(320, 36));
        JButton btnBuscar = UIUtils.botonPrimario("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        JButton btnTodos  = UIUtils.botonSecundario("Ver todos");
        btnTodos.addActionListener(e -> { campoBusqueda.setText(""); cargarTabla(repo.getPacientes()); });

        barraBusq.add(campoBusqueda);
        barraBusq.add(Box.createHorizontalStrut(8));
        barraBusq.add(btnBuscar);
        barraBusq.add(Box.createHorizontalStrut(8));
        barraBusq.add(btnTodos);

        // Tabla de pacientes
        String[] columnas = {"ID", "Nombre completo", "Cédula", "Teléfono", "EPS", "Tipo de sangre"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = UIUtils.tabla(modelo);
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);

        // Doble clic para ver detalle
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) verDetalle();
            }
        });

        // Botonera inferior
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        JButton btnEditar   = UIUtils.botonPrimario("Editar", new MenuIcon(MenuIcon.Tipo.EDITAR, 14, Color.WHITE));
        JButton btnEliminar = UIUtils.botonPeligro("Eliminar", new MenuIcon(MenuIcon.Tipo.ELIMINAR, 14, Color.WHITE));
        JButton btnVer      = UIUtils.botonSecundario("Ver detalle", new MenuIcon(MenuIcon.Tipo.VER, 14, new Color(33,33,33)));
        btnEditar.addActionListener(e   -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnVer.addActionListener(e      -> verDetalle());
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnVer);

        cuerpo.add(barraBusq,               BorderLayout.NORTH);
        cuerpo.add(UIUtils.scrollTabla(tabla), BorderLayout.CENTER);
        cuerpo.add(botones,                 BorderLayout.SOUTH);
        return cuerpo;
    }

    // ── FORMULARIO (agregar / editar) ─────────────────────────────────────────
    private void mostrarFormulario(Paciente p) {
        boolean esEdicion = (p != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Editar Paciente" : "Nuevo Paciente", true);
        dialog.setSize(560, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Título del diálogo
        JPanel tituloPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBackground(UIUtils.AZUL_OSCURO);
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        tituloPanel.add(UIUtils.label(esEdicion ? "Editar datos del paciente" : "Registrar nuevo paciente",
                UIUtils.F_SUBTITULO, Color.WHITE));
        dialog.add(tituloPanel, BorderLayout.NORTH);

        // Formulario con scroll
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        campoNombre    = UIUtils.campo();
        campoCedula    = UIUtils.campo();
        campoFecha     = UIUtils.campo();
        campoTelefono  = UIUtils.campo();
        campoCorreo    = UIUtils.campo();
        campoDireccion = UIUtils.campo();
        comboGenero    = UIUtils.combo("Masculino", "Femenino", "Otro");
        comboSangre    = UIUtils.combo("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-");
        comboEps       = UIUtils.combo("Sura", "Sanitas", "Compensar", "Famisanar", "Cafesalud", "Nueva EPS", "Otra");

        // Precargar si es edición
        if (esEdicion) {
            campoNombre.setText(p.getNombre());
            campoCedula.setText(p.getCedula());
            campoFecha.setText(p.getFechaNacimiento());
            campoTelefono.setText(p.getTelefono());
            campoCorreo.setText(p.getCorreo());
            campoDireccion.setText(p.getDireccion());
            comboGenero.setSelectedItem(p.getGenero());
            comboSangre.setSelectedItem(p.getTipoSangre());
            comboEps.setSelectedItem(p.getEps());
        }

        // Filas del formulario
        agregarCampo(form, "Nombre completo *",   campoNombre);
        agregarCampo(form, "Cédula *",            campoCedula);
        agregarCampo(form, "Fecha de nacimiento", campoFecha);
        agregarCampo(form, "Género",              comboGenero);
        agregarCampo(form, "Teléfono",            campoTelefono);
        agregarCampo(form, "Correo electrónico",  campoCorreo);
        agregarCampo(form, "EPS",                 comboEps);
        agregarCampo(form, "Tipo de sangre",      comboSangre);
        agregarCampo(form, "Dirección",           campoDireccion);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        pie.setBackground(UIUtils.GRIS_FONDO);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.GRIS_BORDE));
        JButton btnCancelar = UIUtils.botonSecundario("Cancelar");
        JButton btnGuardar  = UIUtils.botonExito(esEdicion ? "Guardar cambios" : "Registrar paciente");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e  -> {
            if (guardarPaciente(p)) dialog.dispose();
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
        fila.add(lbl,  BorderLayout.WEST);
        fila.add(campo,BorderLayout.CENTER);
        form.add(fila);
    }

    private boolean guardarPaciente(Paciente existente) {
        String nombre = campoNombre.getText().trim();
        String cedula = campoCedula.getText().trim();
        if (nombre.isEmpty() || cedula.isEmpty()) {
            UIUtils.alerta(this, "Nombre y cédula son obligatorios.");
            return false;
        }
        if (existente == null) {
            Paciente nuevo = new Paciente(
                repo.nuevoIdPaciente(), nombre, cedula,
                campoFecha.getText().trim(),
                (String) comboGenero.getSelectedItem(),
                campoTelefono.getText().trim(),
                campoCorreo.getText().trim(),
                (String) comboSangre.getSelectedItem(),
                (String) comboEps.getSelectedItem(),
                campoDireccion.getText().trim());
            repo.agregarPaciente(nuevo);
        } else {
            existente.setNombre(nombre);
            existente.setCedula(cedula);
            existente.setFechaNacimiento(campoFecha.getText().trim());
            existente.setGenero((String) comboGenero.getSelectedItem());
            existente.setTelefono(campoTelefono.getText().trim());
            existente.setCorreo(campoCorreo.getText().trim());
            existente.setEps((String) comboEps.getSelectedItem());
            existente.setTipoSangre((String) comboSangre.getSelectedItem());
            existente.setDireccion(campoDireccion.getText().trim());
        }
        cargarTabla(repo.getPacientes());
        UIUtils.exito(this, "Paciente guardado correctamente.");
        return true;
    }

    // ── OPERACIONES DE TABLA ───────────────────────────────────────────────────
    private void buscar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty() || texto.equals("Buscar por nombre o cédula...")) {
            cargarTabla(repo.getPacientes());
        } else {
            cargarTabla(repo.buscarPacientes(texto));
        }
    }

    private void cargarTabla(List<Paciente> lista) {
        modelo.setRowCount(0);
        for (Paciente p : lista) {
            modelo.addRow(new Object[]{
                p.getId(), p.getNombre(), p.getCedula(),
                p.getTelefono(), p.getEps(), p.getTipoSangre()
            });
        }
    }

    private Paciente getPacienteSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { UIUtils.alerta(this, "Seleccione un paciente de la lista."); return null; }
        int id = (int) modelo.getValueAt(fila, 0);
        return repo.getPacientePorId(id);
    }

    private void editarSeleccionado() {
        Paciente p = getPacienteSeleccionado();
        if (p != null) mostrarFormulario(p);
    }

    private void eliminarSeleccionado() {
        Paciente p = getPacienteSeleccionado();
        if (p == null) return;
        if (UIUtils.confirmar(this, "¿Eliminar a " + p.getNombre() + "?")) {
            repo.eliminarPaciente(p.getId());
            cargarTabla(repo.getPacientes());
            UIUtils.exito(this, "Paciente eliminado.");
        }
    }

    private void verDetalle() {
        Paciente p = getPacienteSeleccionado();
        if (p == null) return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detalle del Paciente", true);
        d.setSize(420, 480);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        agregarDetalle(panel, "ID",               String.valueOf(p.getId()));
        agregarDetalle(panel, "Nombre",            p.getNombre());
        agregarDetalle(panel, "Cédula",            p.getCedula());
        agregarDetalle(panel, "Fecha nacimiento",  p.getFechaNacimiento());
        agregarDetalle(panel, "Género",            p.getGenero());
        agregarDetalle(panel, "Teléfono",          p.getTelefono());
        agregarDetalle(panel, "Correo",            p.getCorreo());
        agregarDetalle(panel, "EPS",               p.getEps());
        agregarDetalle(panel, "Tipo de sangre",    p.getTipoSangre());
        agregarDetalle(panel, "Dirección",         p.getDireccion());

        List<HistoriaClinica> historias = repo.getHistoriasPorPaciente(p.getNombre());
        agregarDetalle(panel, "Historias clínicas", String.valueOf(historias.size()));

        JButton cerrar = UIUtils.botonPrimario("Cerrar");
        cerrar.addActionListener(e -> d.dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pie.setBackground(Color.WHITE);
        pie.add(cerrar);

        d.add(new JScrollPane(panel), BorderLayout.CENTER);
        d.add(pie, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void agregarDetalle(JPanel p, String clave, String valor) {
        p.add(UIUtils.label(clave + ":", UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS));
        p.add(UIUtils.label(valor,       UIUtils.F_NORMAL,  UIUtils.TEXTO_OSCURO));
    }
}