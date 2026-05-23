package Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Módulo de Facturación — CRUD completo con cálculo automático de totales.
 */
public class FacturacionPanel extends JPanel {

    private final DataRepository repo = DataRepository.getInstance();
    private DefaultTableModel modelo;
    private JTable tabla;

    private static final String[] SERVICIOS = {
        "Consulta General", "Consulta Especialista", "Urgencias",
        "Radiografía", "Ecografía", "Laboratorio Clínico",
        "Ecocardiograma", "Vacunación", "Cirugía Ambulatoria",
        "Hospitalización (día)", "Terapia Física"
    };

    public FacturacionPanel() {
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

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(UIUtils.label("Facturación", UIUtils.F_TITULO, UIUtils.AZUL_OSCURO));
        textos.add(UIUtils.label("Generación y gestión de facturas de servicios médicos", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));

        JButton btnNueva = UIUtils.botonPrimario("+ Nueva Factura");
        btnNueva.addActionListener(e -> mostrarFormulario(null));

        h.add(textos, BorderLayout.CENTER);
        h.add(btnNueva, BorderLayout.EAST);
        return h;
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // Filtros por estado
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtros.setOpaque(false);
        filtros.add(UIUtils.label("Filtrar: ", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));
        for (String estado : new String[]{"Todas", "PENDIENTE", "PAGADA", "ANULADA"}) {
            JButton btn = UIUtils.botonSecundario(estado);
            btn.addActionListener(e -> filtrarPorEstado(estado));
            filtros.add(btn);
        }

        // Tabla
        String[] cols = {"ID", "Paciente", "Servicio", "Subtotal", "Descuento", "IVA %", "Total", "Estado", "Método Pago"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = UIUtils.tabla(modelo);
        tabla.getColumnModel().getColumn(0).setMaxWidth(45);

        // Botones de acción
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        JButton btnPagar   = UIUtils.botonExito("Marcar Pagada", new MenuIcon(MenuIcon.Tipo.PAGAR, 14, Color.WHITE));
        JButton btnAnular  = UIUtils.botonPeligro("Anular", new MenuIcon(MenuIcon.Tipo.ANULAR, 14, Color.WHITE));
        JButton btnVer     = UIUtils.botonSecundario("Ver detalle", new MenuIcon(MenuIcon.Tipo.VER, 14, new Color(33,33,33)));
        JButton btnElim    = UIUtils.botonPeligro("Eliminar", new MenuIcon(MenuIcon.Tipo.ELIMINAR, 14, Color.WHITE));

        btnPagar.addActionListener(e  -> cambiarEstado(Factura.Estado.PAGADA));
        btnAnular.addActionListener(e -> cambiarEstado(Factura.Estado.ANULADA));
        btnVer.addActionListener(e    -> verDetalle());
        btnElim.addActionListener(e   -> eliminarSeleccionada());

        botones.add(btnPagar);
        botones.add(btnAnular);
        botones.add(btnVer);
        botones.add(btnElim);

        cuerpo.add(filtros,                    BorderLayout.NORTH);
        cuerpo.add(UIUtils.scrollTabla(tabla), BorderLayout.CENTER);
        cuerpo.add(botones,                    BorderLayout.SOUTH);
        return cuerpo;
    }

    // ── FORMULARIO ─────────────────────────────────────────────────────────────
    private void mostrarFormulario(Factura f) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Factura", true);
        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(UIUtils.AZUL_OSCURO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        header.add(UIUtils.label("Generar nueva factura", UIUtils.F_SUBTITULO, Color.WHITE));
        dialog.add(header, BorderLayout.NORTH);

        // Campos
        List<Paciente> pacientes = repo.getPacientes();
        String[] nombres = pacientes.stream().map(Paciente::getNombre).toArray(String[]::new);
        JComboBox<String> comboPaciente  = UIUtils.combo(nombres);
        JComboBox<String> comboServicio  = UIUtils.combo(SERVICIOS);
        JComboBox<String> comboMetodo    = UIUtils.combo("Efectivo", "Tarjeta débito", "Tarjeta crédito", "Transferencia", "EPS");
        JTextField campoSubtotal   = UIUtils.campo();
        JTextField campoDescuento  = UIUtils.campo();
        JTextField campoIva        = UIUtils.campo();
        JTextField campoFecha      = UIUtils.campo();
        JLabel     lblTotal        = UIUtils.label("$ 0", UIUtils.F_SUBTITULO, UIUtils.VERDE);

        campoSubtotal.setToolTipText("Valor en pesos colombianos");
        campoDescuento.setText("0");
        campoIva.setText("0");
        campoFecha.setText(java.time.LocalDate.now().toString());

        // Cálculo automático del total al cambiar valores
        java.awt.event.KeyAdapter calcular = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                try {
                    double sub  = Double.parseDouble(campoSubtotal.getText().trim());
                    double desc = Double.parseDouble(campoDescuento.getText().trim());
                    double iva  = Double.parseDouble(campoIva.getText().trim());
                    double base = sub - desc;
                    double total = base + (base * iva / 100.0);
                    lblTotal.setText(formatearPeso(total));
                    lblTotal.setForeground(UIUtils.VERDE);
                } catch (NumberFormatException ex) {
                    lblTotal.setText("Valor inválido");
                    lblTotal.setForeground(UIUtils.ROJO);
                }
            }
        };
        campoSubtotal.addKeyListener(calcular);
        campoDescuento.addKeyListener(calcular);
        campoIva.addKeyListener(calcular);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        agregarCampo(form, "Paciente *",       comboPaciente);
        agregarCampo(form, "Servicio *",       comboServicio);
        agregarCampo(form, "Subtotal ($) *",   campoSubtotal);
        agregarCampo(form, "Descuento ($)",    campoDescuento);
        agregarCampo(form, "IVA (%)",          campoIva);
        agregarCampo(form, "Método de pago",   comboMetodo);
        agregarCampo(form, "Fecha",            campoFecha);

        // Fila total destacada
        JPanel filaTotal = new JPanel(new BorderLayout(12, 0));
        filaTotal.setBackground(UIUtils.AZUL_CLARO);
        filaTotal.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        filaTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        JLabel lblTotalTxt = UIUtils.label("TOTAL A PAGAR:", UIUtils.F_SUBTITULO, UIUtils.AZUL_OSCURO);
        lblTotalTxt.setPreferredSize(new Dimension(170, 30));
        filaTotal.add(lblTotalTxt, BorderLayout.WEST);
        filaTotal.add(lblTotal,    BorderLayout.CENTER);
        form.add(filaTotal);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        pie.setBackground(UIUtils.GRIS_FONDO);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.GRIS_BORDE));
        JButton btnCancelar = UIUtils.botonSecundario("Cancelar");
        JButton btnGuardar  = UIUtils.botonExito("Generar factura");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            String paciente = (String) comboPaciente.getSelectedItem();
            String servicio = (String) comboServicio.getSelectedItem();
            String subtotalTxt = campoSubtotal.getText().trim();
            if (paciente == null || subtotalTxt.isEmpty()) {
                UIUtils.alerta(dialog, "Paciente y subtotal son obligatorios."); return;
            }
            try {
                double sub  = Double.parseDouble(subtotalTxt);
                double desc = Double.parseDouble(campoDescuento.getText().trim());
                double iva  = Double.parseDouble(campoIva.getText().trim());
                repo.agregarFactura(new Factura(
                    repo.nuevoIdFactura(), paciente, servicio, sub, desc, iva,
                    campoFecha.getText().trim(), Factura.Estado.PENDIENTE,
                    (String) comboMetodo.getSelectedItem()));
                cargarTabla();
                UIUtils.exito(this, "Factura generada correctamente.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                UIUtils.alerta(dialog, "Ingrese valores numéricos válidos para subtotal, descuento e IVA.");
            }
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
        lbl.setPreferredSize(new Dimension(150, 30));
        campo.setPreferredSize(new Dimension(280, 34));
        fila.add(lbl, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        form.add(fila);
    }

    // ── OPERACIONES ─────────────────────────────────────────────────────────────
    private void cargarTabla() { cargarTabla(repo.getFacturas()); }

    private void cargarTabla(List<Factura> lista) {
        modelo.setRowCount(0);
        for (Factura f : lista) {
            modelo.addRow(new Object[]{
                f.getId(), f.getPaciente(), f.getServicio(),
                formatearPeso(f.getSubtotal()),
                formatearPeso(f.getDescuento()),
                f.getIva() + "%",
                formatearPeso(f.getTotal()),
                f.getEstado().toString(),
                f.getMetodoPago()
            });
        }
    }

    private Factura getFacturaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { UIUtils.alerta(this, "Seleccione una factura."); return null; }
        int id = (int) modelo.getValueAt(fila, 0);
        return repo.getFacturas().stream().filter(f -> f.getId() == id).findFirst().orElse(null);
    }

    private void cambiarEstado(Factura.Estado estado) {
        Factura f = getFacturaSeleccionada();
        if (f == null) return;
        f.setEstado(estado);
        cargarTabla();
        UIUtils.exito(this, "Factura marcada como: " + estado);
    }

    private void eliminarSeleccionada() {
        Factura f = getFacturaSeleccionada();
        if (f == null) return;
        if (UIUtils.confirmar(this, "¿Eliminar factura #" + f.getId() + "?")) {
            repo.eliminarFactura(f.getId());
            cargarTabla();
        }
    }

    private void verDetalle() {
        Factura f = getFacturaSeleccionada();
        if (f == null) return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detalle Factura #" + f.getId(), true);
        d.setSize(400, 360);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Object[][] datos = {
            {"# Factura",    String.valueOf(f.getId())},
            {"Paciente",     f.getPaciente()},
            {"Servicio",     f.getServicio()},
            {"Subtotal",     formatearPeso(f.getSubtotal())},
            {"Descuento",    formatearPeso(f.getDescuento())},
            {"IVA",          f.getIva() + "%"},
            {"TOTAL",        formatearPeso(f.getTotal())},
            {"Estado",       f.getEstado().toString()},
            {"Método pago",  f.getMetodoPago()},
            {"Fecha",        f.getFecha()},
        };
        for (Object[] fila : datos) {
            panel.add(UIUtils.label((String)fila[0] + ":", UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS));
            JLabel val = UIUtils.label((String)fila[1], UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO);
            if (fila[0].equals("TOTAL")) val.setForeground(UIUtils.VERDE);
            panel.add(val);
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

    private void filtrarPorEstado(String estado) {
        if (estado.equals("Todas")) { cargarTabla(); return; }
        List<Factura> filtradas = repo.getFacturas().stream()
                .filter(f -> f.getEstado().toString().equals(estado))
                .collect(java.util.stream.Collectors.toList());
        cargarTabla(filtradas);
    }

    private String formatearPeso(double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return nf.format(valor);
    }
}  