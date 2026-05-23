package Test;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Componentes y utilidades de UI compartidos por todos los módulos.
 */
public class UIUtils {

    // ── Paleta de colores ─────────────────────────────────────────────────────
    public static final Color AZUL_OSCURO  = new Color(13,  71,  161);
    public static final Color AZUL_MEDIO   = new Color(21,  101, 192);
    public static final Color AZUL_CLARO   = new Color(227, 242, 253);
    public static final Color VERDE        = new Color(27,  94,  32);
    public static final Color VERDE_CLARO  = new Color(200, 230, 201);
    public static final Color ROJO         = new Color(183, 28,  28);
    public static final Color ROJO_CLARO   = new Color(255, 205, 210);
    public static final Color NARANJA      = new Color(230, 81,  0);
    public static final Color NARANJA_CLARO= new Color(255, 224, 178);
    public static final Color GRIS_FONDO   = new Color(245, 247, 250);
    public static final Color GRIS_BORDE   = new Color(220, 220, 220);
    public static final Color TEXTO_OSCURO = new Color(33,  33,  33);
    public static final Color TEXTO_GRIS   = new Color(117, 117, 117);

    // ── Fuentes ───────────────────────────────────────────────────────────────
    public static final Font F_TITULO   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font F_SUBTITULO= new Font("Segoe UI", Font.BOLD,  15);
    public static final Font F_NORMAL   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_PEQUEÑA  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_BOTON    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_NUMERO   = new Font("Segoe UI", Font.BOLD,  28);

    private UIUtils() {}

    // ── Etiquetas ─────────────────────────────────────────────────────────────
    public static JLabel label(String texto, Font f, Color c) {
        JLabel l = new JLabel(texto);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    public static JLabel labelCentrado(String texto, Font f, Color c) {
        JLabel l = label(texto, f, c);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    // ── Campos de texto ───────────────────────────────────────────────────────
    public static JTextField campo() {
        JTextField tf = new JTextField();
        tf.setFont(F_NORMAL);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setPreferredSize(new Dimension(200, 34));
        return tf;
    }

    public static JTextArea area(int filas, int cols) {
        JTextArea ta = new JTextArea(filas, cols);
        ta.setFont(F_NORMAL);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return ta;
    }

    public static JComboBox<String> combo(String... opciones) {
        JComboBox<String> cb = new JComboBox<>(opciones);
        cb.setFont(F_NORMAL);
        cb.setPreferredSize(new Dimension(200, 34));
        return cb;
    }

    public static JPasswordField campoPass() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(F_NORMAL);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        pf.setPreferredSize(new Dimension(200, 34));
        return pf;
    }

    // ── Botones ───────────────────────────────────────────────────────────────
    public static JButton botonPrimario(String texto) {
        return botonPrimario(texto, null);
    }

    public static JButton botonPrimario(String texto, Icon icono) {
        JButton b = icono != null ? new JButton(texto, icono) : new JButton(texto);
        if (icono != null) { b.setIconTextGap(7); b.setHorizontalTextPosition(SwingConstants.RIGHT); }
        b.setFont(F_BOTON);
        b.setBackground(AZUL_MEDIO);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        aplicarEfectoHover(b, AZUL_MEDIO, AZUL_OSCURO);
        return b;
    }

    public static JButton botonPeligro(String texto) {
        return botonPeligro(texto, null);
    }

    public static JButton botonPeligro(String texto, Icon icono) {
        JButton b = icono != null ? new JButton(texto, icono) : new JButton(texto);
        if (icono != null) { b.setIconTextGap(7); b.setHorizontalTextPosition(SwingConstants.RIGHT); }
        b.setFont(F_BOTON);
        b.setBackground(ROJO);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        aplicarEfectoHover(b, ROJO, new Color(211, 47, 47));
        return b;
    }

    public static JButton botonSecundario(String texto) {
        return botonSecundario(texto, null);
    }

    public static JButton botonSecundario(String texto, Icon icono) {
        JButton b = icono != null ? new JButton(texto, icono) : new JButton(texto);
        if (icono != null) { b.setIconTextGap(7); b.setHorizontalTextPosition(SwingConstants.RIGHT); }
        b.setFont(F_BOTON);
        b.setBackground(new Color(236, 239, 241));
        b.setForeground(TEXTO_OSCURO);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    public static JButton botonExito(String texto) {
        return botonExito(texto, null);
    }

    public static JButton botonExito(String texto, Icon icono) {
        JButton b = icono != null ? new JButton(texto, icono) : new JButton(texto);
        if (icono != null) { b.setIconTextGap(7); b.setHorizontalTextPosition(SwingConstants.RIGHT); }
        b.setFont(F_BOTON);
        b.setBackground(VERDE);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private static void aplicarEfectoHover(JButton b, Color normal, Color hover) {
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(normal); }
        });
    }

    // ── Tablas ────────────────────────────────────────────────────────────────
    public static JTable tabla(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setFont(F_NORMAL);
        t.setRowHeight(36);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(AZUL_CLARO);
        t.setSelectionForeground(TEXTO_OSCURO);
        t.setFillsViewportHeight(true);

        // Encabezado personalizado — renderer propio para que no lo sobreescriba el L&F del SO
        JTableHeader header = t.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = new JLabel(value != null ? value.toString() : "");
                lbl.setFont(F_BOTON);
                lbl.setForeground(Color.WHITE);
                lbl.setBackground(AZUL_OSCURO);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 80, 170)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setPreferredSize(new Dimension(lbl.getPreferredSize().width, 42));
                return lbl;
            }
        });

        // Filas alternas
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return this;
            }
        });
        return t;
    }

    public static JScrollPane scrollTabla(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    // ── Tarjeta de estadística (para el Dashboard) ────────────────────────────
    public static JPanel tarjetaStat(String titulo, String valor, Color colorAcento) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorAcento, 2),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JLabel lblValor = labelCentrado(valor,  F_NUMERO,    colorAcento);
        JLabel lblTitulo = labelCentrado(titulo, F_PEQUEÑA,   TEXTO_GRIS);

        card.add(lblValor,  BorderLayout.CENTER);
        card.add(lblTitulo, BorderLayout.SOUTH);
        return card;
    }

    // ── Panel sección con título y borde ──────────────────────────────────────
    public static JPanel panelSeccion(String titulo) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GRIS_BORDE),
                " " + titulo + " ",
                0, 0,
                F_SUBTITULO, AZUL_OSCURO),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        return p;
    }

    // ── Separador ─────────────────────────────────────────────────────────────
    public static JSeparator separador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(GRIS_BORDE);
        return sep;
    }

    // ── Diálogo de confirmación estilizado ────────────────────────────────────
    public static boolean confirmar(Component parent, String mensaje) {
        return JOptionPane.showConfirmDialog(parent, mensaje, "Confirmar",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void alerta(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void exito(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Fila de formulario (etiqueta + campo) ─────────────────────────────────
    public static void agregarFila(JPanel panel, String etiqueta, JComponent campo) {
        JLabel lbl = label(etiqueta, F_NORMAL, TEXTO_OSCURO);
        lbl.setPreferredSize(new Dimension(130, 30));
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        fila.setOpaque(false);
        fila.add(lbl);
        fila.add(campo);
        panel.add(fila);
    }

    // ── Barra de búsqueda ─────────────────────────────────────────────────────
    public static JTextField barraBusqueda(String placeholder) {
        JTextField tf = new JTextField(placeholder, 20);
        tf.setFont(F_NORMAL);
        tf.setForeground(TEXTO_GRIS);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(TEXTO_OSCURO); }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(TEXTO_GRIS); }
            }
        });
        return tf;
    }

    // ── Badge de estado ───────────────────────────────────────────────────────
    public static JLabel badge(String texto, Color fondo, Color fuente) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(fuente);
        l.setBackground(fondo);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }
}