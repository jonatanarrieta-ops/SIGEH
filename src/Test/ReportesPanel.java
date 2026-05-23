package Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Módulo de Reportes — resúmenes estadísticos del sistema.
 */
public class ReportesPanel extends JPanel {

    private final DataRepository repo = DataRepository.getInstance();

    public ReportesPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.GRIS_FONDO);
        construir();
    }

    private void construir() {
        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirCuerpo(),     BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Color.WHITE);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.GRIS_BORDE),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(UIUtils.label("Reportes y Estadísticas", UIUtils.F_TITULO, UIUtils.AZUL_OSCURO));
        textos.add(UIUtils.label("Resumen operativo del sistema hospitalario", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));
        JButton btnActualizar = UIUtils.botonPrimario("Actualizar", new MenuIcon(MenuIcon.Tipo.ACTUALIZAR, 14, Color.WHITE));
        btnActualizar.addActionListener(e -> { removeAll(); construir(); revalidate(); repaint(); });
        h.add(textos, BorderLayout.CENTER);
        h.add(btnActualizar, BorderLayout.EAST);
        return h;
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new GridLayout(2, 2, 16, 16));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        cuerpo.add(panelResumenCitas());
        cuerpo.add(panelResumenFacturacion());
        cuerpo.add(panelTopPacientes());
        cuerpo.add(panelResumenGeneral());
        return cuerpo;
    }

    private JPanel panelResumenCitas() {
        JPanel panel = UIUtils.panelSeccion("Citas por Estado");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Map<Cita.Estado, Long> conteo = repo.getCitas().stream()
                .collect(Collectors.groupingBy(Cita::getEstado, Collectors.counting()));

        for (Cita.Estado estado : Cita.Estado.values()) {
            long total = conteo.getOrDefault(estado, 0L);
            panel.add(filaReporte(estado.toString(), String.valueOf(total), colorEstado(estado)));
            panel.add(UIUtils.separador());
        }
        panel.add(filaReporte("TOTAL", String.valueOf(repo.getCitas().size()), UIUtils.AZUL_OSCURO));
        return panel;
    }

    private JPanel panelResumenFacturacion() {
        JPanel panel = UIUtils.panelSeccion("Facturación por Estado");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        List<Factura> facturas = repo.getFacturas();
        double totalPagado    = facturas.stream().filter(f -> f.getEstado() == Factura.Estado.PAGADA).mapToDouble(Factura::getTotal).sum();
        double totalPendiente = facturas.stream().filter(f -> f.getEstado() == Factura.Estado.PENDIENTE).mapToDouble(Factura::getTotal).sum();
        double totalAnulado   = facturas.stream().filter(f -> f.getEstado() == Factura.Estado.ANULADA).mapToDouble(Factura::getTotal).sum();

        panel.add(filaReporte("Pagadas",    formatearPeso(totalPagado),    UIUtils.VERDE));
        panel.add(UIUtils.separador());
        panel.add(filaReporte("Pendientes", formatearPeso(totalPendiente), UIUtils.NARANJA));
        panel.add(UIUtils.separador());
        panel.add(filaReporte("Anuladas",   formatearPeso(totalAnulado),   UIUtils.ROJO));
        panel.add(UIUtils.separador());
        panel.add(filaReporte("TOTAL COBRADO", formatearPeso(totalPagado), UIUtils.AZUL_OSCURO));
        return panel;
    }

    private JPanel panelTopPacientes() {
        JPanel panel = UIUtils.panelSeccion("Pacientes con más visitas");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Map<String, Long> conteo = repo.getHistorias().stream()
                .collect(Collectors.groupingBy(HistoriaClinica::getPaciente, Collectors.counting()));

        conteo.entrySet().stream()
              .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
              .limit(5)
              .forEach(entry -> {
                  panel.add(filaReporte(entry.getKey(), entry.getValue() + " visita(s)", UIUtils.AZUL_MEDIO));
                  panel.add(UIUtils.separador());
              });

        if (conteo.isEmpty()) {
            panel.add(UIUtils.label("Sin datos disponibles", UIUtils.F_NORMAL, UIUtils.TEXTO_GRIS));
        }
        return panel;
    }

    private JPanel panelResumenGeneral() {
        JPanel panel = UIUtils.panelSeccion("Resumen General del Sistema");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(filaReporte("Total de pacientes",      String.valueOf(repo.totalPacientes()),      UIUtils.AZUL_OSCURO));
        panel.add(UIUtils.separador());
        panel.add(filaReporte("Total de citas",          String.valueOf(repo.getCitas().size()),     UIUtils.AZUL_MEDIO));
        panel.add(UIUtils.separador());
        panel.add(filaReporte("Total de facturas",       String.valueOf(repo.getFacturas().size()),  UIUtils.NARANJA));
        panel.add(UIUtils.separador());
        panel.add(filaReporte("Historias clínicas",      String.valueOf(repo.getHistorias().size()), UIUtils.VERDE));
        panel.add(UIUtils.separador());
        panel.add(filaReporte("Ingresos totales",        formatearPeso(repo.ingresosTotales()),      UIUtils.VERDE));
        return panel;
    }

    private JPanel filaReporte(String etiqueta, String valor, Color colorValor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        fila.add(UIUtils.label(etiqueta, UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO), BorderLayout.WEST);
        fila.add(UIUtils.label(valor,    UIUtils.F_SUBTITULO, colorValor),         BorderLayout.EAST);
        return fila;
    }

    private Color colorEstado(Cita.Estado e) {
        return switch (e) {
            case CONFIRMADA -> UIUtils.VERDE;
            case PENDIENTE  -> UIUtils.NARANJA;
            case CANCELADA  -> UIUtils.ROJO;
            case COMPLETADA -> UIUtils.AZUL_MEDIO;
        };
    }

    private String formatearPeso(double valor) {
        return NumberFormat.getCurrencyInstance(new Locale("es", "CO")).format(valor);
    }
}