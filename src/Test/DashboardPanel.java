package Test;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Panel principal del Dashboard — muestra estadísticas y actividad reciente.
 */
public class DashboardPanel extends JPanel {

    private final DataRepository repo = DataRepository.getInstance();

    // Tarjetas de estadísticas (referencia para actualizarlas)
    private JPanel cardPacientes;
    private JPanel cardCitas;
    private JPanel cardFacturas;
    private JPanel cardIngresos;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIUtils.GRIS_FONDO);
        construir();
    }

    private void construir() {
        // ── Encabezado ────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.GRIS_BORDE),
            BorderFactory.createEmptyBorder(18, 24, 18, 24)));

        JLabel titulo = UIUtils.label("Dashboard General", UIUtils.F_TITULO, UIUtils.AZUL_OSCURO);
        JLabel fecha  = UIUtils.label("Sistema SIGEH — Hospital Universitario", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(fecha);
        header.add(textos, BorderLayout.CENTER);

        // Botón de actualizar
        JButton btnActualizar = UIUtils.botonSecundario("Actualizar", new MenuIcon(MenuIcon.Tipo.ACTUALIZAR, 14, new Color(33,33,33)));
        btnActualizar.addActionListener(e -> actualizar());
        header.add(btnActualizar, BorderLayout.EAST);

        // ── Tarjetas de estadísticas ──────────────────────────────────────────
        JPanel gridStats = new JPanel(new GridLayout(1, 4, 16, 0));
        gridStats.setOpaque(false);
        gridStats.setBorder(BorderFactory.createEmptyBorder(24, 24, 0, 24));

        cardPacientes = UIUtils.tarjetaStat("PACIENTES REGISTRADOS",
                String.valueOf(repo.totalPacientes()), UIUtils.AZUL_OSCURO);
        cardCitas = UIUtils.tarjetaStat("CITAS ACTIVAS",
                String.valueOf(repo.citasPendientes()), UIUtils.VERDE);
        cardFacturas = UIUtils.tarjetaStat("FACTURAS PENDIENTES",
                String.valueOf(repo.facturasPendientes()), UIUtils.NARANJA);
        cardIngresos = UIUtils.tarjetaStat("INGRESOS (PAGADAS)",
                formatearPeso(repo.ingresosTotales()), UIUtils.AZUL_MEDIO);

        gridStats.add(cardPacientes);
        gridStats.add(cardCitas);
        gridStats.add(cardFacturas);
        gridStats.add(cardIngresos);

        // ── Actividad reciente ────────────────────────────────────────────────
        JPanel centro = new JPanel(new GridLayout(1, 2, 16, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        centro.add(panelCitasRecientes());
        centro.add(panelFacturasRecientes());

        // ── Pie de página del dashboard ───────────────────────────────────────
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.GRIS_BORDE));
        JLabel lblPie = UIUtils.label("SIGEH v2.0 — SoftHealth Solutions S.A.S. © 2026", UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS);
        pie.add(lblPie);

        add(header,    BorderLayout.NORTH);
        add(gridStats, BorderLayout.NORTH); // Nota: se agrega en el wrapper abajo
        add(pie,       BorderLayout.SOUTH);

        // Reorganizar con wrapper vertical
        removeAll();
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(UIUtils.GRIS_FONDO);
        wrapper.add(header,    BorderLayout.NORTH);
        wrapper.add(gridStats, BorderLayout.CENTER); // temporalmente

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(UIUtils.GRIS_FONDO);
        contenido.add(gridStats, BorderLayout.NORTH);
        contenido.add(centro,    BorderLayout.CENTER);

        add(header,   BorderLayout.NORTH);
        add(contenido,BorderLayout.CENTER);
        add(pie,      BorderLayout.SOUTH);
    }

    private JPanel panelCitasRecientes() {
        JPanel panel = UIUtils.panelSeccion("Próximas Citas");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        java.util.List<Cita> citas = repo.getCitas();
        int mostrar = Math.min(citas.size(), 5);
        for (int i = 0; i < mostrar; i++) {
            Cita c = citas.get(i);
            panel.add(filaCita(c));
            if (i < mostrar - 1) panel.add(UIUtils.separador());
        }
        if (citas.isEmpty()) {
            panel.add(UIUtils.label("No hay citas registradas", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));
        }
        return panel;
    }

    private JPanel filaCita(Cita c) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JPanel izq = new JPanel(new GridLayout(2, 1, 0, 2));
        izq.setOpaque(false);
        izq.add(UIUtils.label(c.getPaciente(), UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO));
        izq.add(UIUtils.label(c.getMedico() + " · " + c.getEspecialidad(), UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS));

        JPanel der = new JPanel(new GridLayout(2, 1, 0, 2));
        der.setOpaque(false);
        der.add(UIUtils.label(c.getFecha(), UIUtils.F_PEQUEÑA, UIUtils.TEXTO_OSCURO));
        der.add(UIUtils.label(c.getHora(),  UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS));

        Color[] colores = colorEstadoCita(c.getEstado());
        JLabel badge = UIUtils.badge(c.getEstado().toString(), colores[0], colores[1]);

        fila.add(izq, BorderLayout.CENTER);
        fila.add(der, BorderLayout.EAST);
        return fila;
    }

    private JPanel panelFacturasRecientes() {
        JPanel panel = UIUtils.panelSeccion("Facturas Recientes");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        java.util.List<Factura> facturas = repo.getFacturas();
        int mostrar = Math.min(facturas.size(), 5);
        for (int i = 0; i < mostrar; i++) {
            Factura f = facturas.get(i);
            panel.add(filaFactura(f));
            if (i < mostrar - 1) panel.add(UIUtils.separador());
        }
        if (facturas.isEmpty()) {
            panel.add(UIUtils.label("No hay facturas registradas", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));
        }
        return panel;
    }

    private JPanel filaFactura(Factura f) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JPanel izq = new JPanel(new GridLayout(2, 1, 0, 2));
        izq.setOpaque(false);
        izq.add(UIUtils.label(f.getPaciente(), UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO));
        izq.add(UIUtils.label(f.getServicio(), UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS));

        Color[] colores = colorEstadoFactura(f.getEstado());
        JLabel lblTotal = UIUtils.label(formatearPeso(f.getTotal()), UIUtils.F_SUBTITULO,
                f.getEstado() == Factura.Estado.PAGADA ? UIUtils.VERDE : UIUtils.NARANJA);

        fila.add(izq,     BorderLayout.CENTER);
        fila.add(lblTotal,BorderLayout.EAST);
        return fila;
    }

    private Color[] colorEstadoCita(Cita.Estado e) {
        return switch (e) {
            case CONFIRMADA  -> new Color[]{UIUtils.VERDE_CLARO,   UIUtils.VERDE};
            case PENDIENTE   -> new Color[]{UIUtils.NARANJA_CLARO, UIUtils.NARANJA};
            case CANCELADA   -> new Color[]{UIUtils.ROJO_CLARO,    UIUtils.ROJO};
            case COMPLETADA  -> new Color[]{UIUtils.AZUL_CLARO,    UIUtils.AZUL_OSCURO};
        };
    }

    private Color[] colorEstadoFactura(Factura.Estado e) {
        return switch (e) {
            case PAGADA    -> new Color[]{UIUtils.VERDE_CLARO,   UIUtils.VERDE};
            case PENDIENTE -> new Color[]{UIUtils.NARANJA_CLARO, UIUtils.NARANJA};
            case ANULADA   -> new Color[]{UIUtils.ROJO_CLARO,    UIUtils.ROJO};
        };
    }

    private String formatearPeso(double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return nf.format(valor);
    }

    /** Actualiza los valores de las tarjetas con datos frescos del repositorio */
    public void actualizar() {
        // Reconstruye el panel completo con datos actualizados
        removeAll();
        construir();
        revalidate();
        repaint();
    }
}   